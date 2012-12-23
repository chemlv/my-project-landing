package com.tai.landing.screens;

//  http://youtu.be/JRnuoUI7pTY

import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tai.landing.gamelogic.Landing;

public class PhysicsDemo extends BaseScreen {
	
	public static Music silent;
	public static Music jingle;
	public static Sound jump;
	public static Sound fireworks;

	// ---- Tiled Map ------------
	TiledMap tiledMap;
	TileAtlas tileAtlas;
	TileMapRenderer tileMapRenderer;

	// -------Box2d--------------

	// Tạo mới một "thế giới với lực nằm ngang (gió) = 0 (không có gió) và lực
	// hút -10 (âm vì y là chiều chỉa lên - mà ta muốn vật rơi xuống).
	// Tham số thứ hai True/False là cho phép vật thể NGỦ thay không...Khi vật
	// thể NGỦ rồi (rơi xuống, tưng tưng một hồi, không có tác động gì mới và nó
	// nằm yên
	// => ngủ (máy tính tạm thời bỏ nó qua không tính nữa).. sau này bạn phải
	// "Đánh thức nó dậy" nếu muốn nó hoạt động lại
	// hoặc "đập cho nó một phát" (hàm ApplyForce) thì khỏi cần đánh thức nó
	// cũng sẽ thức dậy....
	World world = new World(new Vector2(0, -10), true);

	// camera.
	OrthographicCamera camera;

	// Thế giới của bạn sẽ "bùm bụp bùm bụp quả tim" theo nhịp bao nhiêu
	// Có người lấy đúng FramePerSecond (thường 1/60f)
	// libgdx khuyên 1/45f
	static final float BOX_STEP = 1 / 45f;

	// Cái 6,2 này làm lơ đi bạn, nếu cứ khăng khắng muốn hiểu thì vào Box2d
	// document mà đọc (trích lời của libgdx).
	static final int BOX_VELOCITY_ITERATIONS = 6;
	static final int BOX_POSITION_ITERATIONS = 2;

	// Trong thế giới Box2D một đơn vị là một MÉT......
	// Ví dụ bạn định nghĩa một quả banh circle.setRadius(6f); Tức là bạn định
	// nghĩa một quả banh có bán kính 6 mét.
	// Và để mọi thứ HOẠT ĐỘNG giống như thực tế thì bạn phải định nghĩa sao cho
	// kích thước các vật thể này tương đối giống như thực tế.
	// Không nên để vật 40 mét rơi từ 480 mét xuống (như lúc đầu không biết đã
	// phạm phải, bạn cứ tưởng tưởng trong thực tế quả bóng to như vậy
	// rơi từ trên chín tầng mây xuống thì chạm đất mất bao lâu...lờ đờ là phải,
	// lúc đó lại tăng lực hút lên 100 mới ghê
	// - làm theo lời xúi bậy của một "bài văn mẫu" trên mạng đây mà.)

	// Phần đông sử dụng 100 pixel (kích thước của hình trong game) = 1 mét
	// Thôi theo phần đông mà mần vậy
	static final float WORLD_TO_BOX = 0.01f; // ("tỷ lệ" đổi từ đơn vị game sang
												// box2d)
	static final float BOX_TO_WORLD = 100f; // ("tỷ lê" đổi từ đơn vị box2d sang
											// game)

	// Cho "quả cầu" có kích thước 40 pixel
	static final float IMAGESIZE = 16;
	// Vị chi kích thước của "quả cầu" này trong thế giới Box2D sẽ là ....
	static final float BOXSIZE = IMAGESIZE * WORLD_TO_BOX;
	
	static final float GIFTSIZE = 24;
	static final float BOXGIFTSIZE = GIFTSIZE * WORLD_TO_BOX;
	
	
	//Ống khói.
	Image chim;
	
	boolean isWin = false;
	
	static float TIMERGIFT = 0.4f;
	float timerForGift = 0; 
	
	public PhysicsDemo(Landing game) {
		super(game);
		silent = Gdx.audio.newMusic(Gdx.files.internal("sound/silent.mp3"));
		silent.setLooping(true);
		jingle = Gdx.audio.newMusic(Gdx.files.internal("sound/jingle.mp3"));
		jingle.setLooping(true);
		
		jump = Gdx.audio.newSound(Gdx.files.internal("sound/jump.ogg"));
		fireworks = Gdx.audio.newSound(Gdx.files.internal("sound/fireworks.ogg"));
		
	}

	@Override
	public void show() {

		super.show();

		// Nạp TiledMap
		tiledMap = TiledLoader.createMap(Gdx.files.internal("mapx/xmas.tmx"));
		tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("mapx"));
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8);

		// Lấy camera là của stage, định lại kích thước viewport và chĩa ống
		// kính vào giữa...
		camera = (OrthographicCamera) stage.getCamera();
		camera.viewportHeight = BaseScreen.VIEWPORT_HEIGHT;
		camera.viewportWidth = BaseScreen.VIEWPORT_WIDTH;
		camera.position.set(camera.viewportWidth * .5f,
				camera.viewportHeight * .5f, 0f);
		camera.update();

		LoadStaticBodies();

		chim = new Image(getAtlas().findRegion("hinh2/chim"));
		chim.setSize(45, 258);
		chim.setPosition(578, 0);

		// Đưa diễn viên mới này lên sân khấu
		stage.addActor(chim);
		
		silent.play();

	}

	private void LoadStaticBodies() {
		// Duyệt các layer có trong Map
		for (int i = 0; i < tiledMap.objectGroups.size(); i++) {
			TiledObjectGroup group = tiledMap.objectGroups.get(i);

			// Tìm cái layout vẽ đường biên "static body"
			if ("static".equals(group.name)) {
				// Duyệt các Object có trong layer này...
				for (int j = 0; j < group.objects.size(); j++) {
					TiledObject object = group.objects.get(j);
					// Tạo một "static body" theo object mới lấy ra được.
					CreateStaticBody(object);
				}
			}
		}
	}

	private void CreateStaticBody(TiledObject o) {
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;

		// Lấy vị trí x của object...
		float x = o.x;
		// và chuyển đổi sang kích thước của Box2D
		x = x * WORLD_TO_BOX;

		// Lấy vị trí y của object... (có chuyển đổi từ
		// "y chĩa xuống trong tiledmap" sang "y chĩa lên" trong game...
		float y = tileMapRenderer.getMapHeightUnits() - o.y - o.height;
		// và chuyển đổi sang kích thước của Box2D
		y = y * WORLD_TO_BOX;

		groundBodyDef.position.set(x, y);
		Body groundBody = world.createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();

		// tinh diem polygon để tạo shap
		// Khi đọc thuộc tính polygon của object ta có một chuỗi dạng
		// "x1,y1 x2,y2 x3,y3 x4, y4 ...  xn, yn"
		// Nên ta sẽ split theo ký tự " " để lấy danh sách (x,y)
		// và split theo ký tự "," để lấy x và y
		String[] strp = o.polygon.split(" ");
		Vector2[] apoints = new Vector2[strp.length];

		// Duyệt các điểm có trong danh sách
		for (int i = 0; i < strp.length; i++) {
			x = Float.parseFloat(strp[i].split(",")[0]); // lấy x
			x = x * WORLD_TO_BOX; // chuyển đổi sang kích thước Box2D
			y = -Float.parseFloat(strp[i].split(",")[1]); // lấy -y (vì đổi từ y
															// chĩa xuống sang y
															// chĩa lên)
			y = y * WORLD_TO_BOX; // chuyển đổi sang kích thước Box2D

			// và nạp các điểm này vào một mãng...
			apoints[i] = new Vector2(x, y);
		}

		// Tạo một static body với mãng vector2 vừa thu được.
		groundBox.set(apoints);
		groundBody.createFixture(groundBox, 0.0f);
		
		//
		if ("chim".equals(o.name)) groundBody.setUserData(o.name);
	}

	private void CreateDynamicBodyWithForce(Vector2 force) {
		// Dynamic Body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;

		float x;
		x = 0;
		
		x = x * WORLD_TO_BOX;
		float y = 20;
		y = y * WORLD_TO_BOX;
		
		bodyDef.position.set(x, y);

		Body body = world.createBody(bodyDef);
		CircleShape dynamicCircle = new CircleShape();
		dynamicCircle.setRadius(BOXSIZE / 2);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicCircle;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.5f;
		body.createFixture(fixtureDef);

		Image im = new Image(getAtlas().findRegion("hinh2/popo02"));
		im.setSize(IMAGESIZE, IMAGESIZE);
		im.setOrigin(IMAGESIZE / 2, IMAGESIZE / 2);
		body.setUserData(im);
		stage.addActor(im);

		//Đưa ống khỏi lên front
		chim.setZIndex(im.getZIndex() + 1);
		
		
		body.applyForceToCenter(force);

	}

	private void force(float x, float y)
	{
		x = x / 50;
		y = (camera.viewportHeight - y) / 50;
		Vector2 force = new Vector2(x, y);
		CreateDynamicBodyWithForce(force);
	}
	
	private void turnLightOn()
	{
		chim.remove();
		
		Image im = new Image(getAtlas().findRegion("hinh1/xmaxbg"));
		im.setSize(BaseScreen.VIEWPORT_WIDTH, BaseScreen.VIEWPORT_HEIGHT);
		im.setPosition(0, 0);

		// Đưa diễn viên mới này lên sân khấu
		stage.addActor(im);
		silent.stop();
		jingle.play();
		
		// Đưa Santa lên sân khấu
	}
	
	private void Reset()
	{
		world.dispose();
		stage.clear();
		world = new World(new Vector2(0, -10), true);
		LoadStaticBodies();
		stage.addActor(chim);
		isWin = false;
		
		jingle.stop();
		silent.play();
		
		// Đưa Santa ra khỏi sân khấu.
		timerForGift = 0;
	}
	
	@Override
	public void render(float delta) {

		// Mỗi lần nhấn lên màn hình là tạo mới một dynamic body...
		if (Gdx.input.justTouched()) {
			
			if (isWin) Reset();
			else
			{
				float x = Gdx.input.getX();
				float y = Gdx.input.getY();
				jump.play();
				force(x, y);
			}
		}

		stage.act(delta);

		// clear the screen with the given RGB color (black)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (isWin)
		{
			timerForGift += delta;
			if (timerForGift > TIMERGIFT)
			{
				timerForGift = 0;
			}
			
		}
		else
		{
			camera.update();
	
			// Hiển thị Tiled Map
			tileMapRenderer.render(camera);
		}
	
		// Thục thi các hoạt động của các vật thể trong thế giới Box2D
		world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
		world.clearForces();
	
		// Duyệt từng bodies trong thế giới box2D để cập nhật tương ứng cho nhân
		// vật của chúng ta trên sân khấu
		Iterator<Body> bi = world.getBodies();
		while (bi.hasNext()) {
			Body b = bi.next();
				
			Image e;
			try
			{
				e = (Image) b.getUserData(); // Lấy image đã gán trong
													// UserData lúc khi tạo.
			}
			catch (Exception ex)
			{
				e = null;
			}
	
			// nếu body này đã có gán image thì....
			if (e != null) {
				
				float size;
				if (isWin) size = BOXGIFTSIZE;
				else size = BOXSIZE;
				
				float x = b.getPosition().x - size / 2; // lấy x (body lấy x
															// nằm giữa nên ta
															// chuyển sang góc
															// trái dưới)
				x = x * BOX_TO_WORLD; // luôn luôn nhớ chuyển lại x từ box sang
										// game
				float y = b.getPosition().y - size / 2; // tương tự với y
				y = y * BOX_TO_WORLD;
	
				// Cập nhật vị trí từ body sang image...
				e.setPosition(x, y);
	
				// Cập nhật độ xoay từ body sang image (có đổi từ Radians sang
				// Degree)
				e.setRotation(MathUtils.radiansToDegrees * b.getAngle());
			}
		}
		
		if (!isWin)
		{
			List<Contact> cons = world.getContactList();
			for (int i = 0; i < cons.size(); i++)
			{
				try
				{
					String name = (String)cons.get(i).getFixtureA().getBody().getUserData();
					//if (name == null) 
					//String name = (String)cons.get(i).getFixtureB().getBody().getUserData();
					
					if ("chim".equals(name)) 
					{
						isWin = true;
						fireworks.play();
						turnLightOn();
					}
				}
				catch (Exception e) {}
			}
		}
		
		stage.draw();

	}
	
	@Override
    public void dispose()
    {
		super.dispose();
		silent.dispose();
		jingle.dispose();
		jump.dispose();
		fireworks.dispose();
		world.dispose();
		tileAtlas.dispose();
		tileMapRenderer.dispose();
		
    }
}