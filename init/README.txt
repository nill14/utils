Lazy initialization library
============================

//LazyJdkProxy example
//==========================
ICalc lazyProxy = LazyJdkProxy.newProxy(ICalc.class, Calc.class);
lazyProxy.add(5, 3); //lazy Calc initialization

//LazyPojo example
//==========================
ILazyPojo<Calc> lazyPojo = LazyPojo.forClass(Calc.class);
ExecutorService executor = Executors.newCachedThreadPool();
lazyPojo.init(executor); //eagerly start asynchronous initialization
lazyPojo.getInstance().add(5, 3); //blocks until the Calc initialization is finished.

// Complex example
//==========================
IPropertyResolver resolver = Mockito.mock(IPropertyResolver.class);
IPojoInitializer<Object> initializer = AnnotationPojoInitializer.withResolver(resolver);
IPojoFactory<Calc> pojoFactory = PojoFactory.create(Calc.class);
ILazyPojo<ICalc> calcPojo = new LazyPojo<>(pojoFactory, initializer);
ICalc calc = LazyJdkProxy.newProxy(ICalc.class, calcPojo);
calc.add(5, 3); //lazy Calc initialization

interface ICalc {
	int add(int a, int b);
}

class Calc implements ICalc {
	public int add(int a, int b) {
		return a + b;
	}
}


// Primitive dependency injection framework
//==========================================

IServiceRegistry registry = new ServiceRegistry();
registry.putService(ISeedService.class, SeedService.class);
registry.putService(IDiceService.class, DiceService.class);
registry.getService(IDiceService.class).rollDice();
		
		
interface IDiceService {
	int rollDice();
}

interface ISeedService {
	long getSeed();
}

class SeedService implements ISeedService {
	public long getSeed() {
		return 42;
	}
}

class DiceService implements IDiceService {
	@Inject
	private ISeedService seedService;
	private Random rand;
	
	@PostConstruct
	public void init() {
		rand = new Random(seedService.getSeed());
	}
	
	public int rollDice() {
		return rand.nextInt(6) + 1;
	}
	
	@PreDestroy
	public void destroy() {
		rand = null;
	}
}