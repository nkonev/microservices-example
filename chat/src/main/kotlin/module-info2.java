import com.google.guice.jpms.example.impl.IDownwardInjectionImpl;
import com.google.guice.jpms.example.services.IDownwardInjection;

module com.google.guice.jpms.examples {
	//This module must be on the classpath
	requires com.google.guice;
	requires aopalliance;

	//Not required on the base module but for API's you would use these
	exports com.google.guice.jpms.example;
	//Because guice is accessing this package if it is not exported to it,
	//an exception will be thrown.
	// It can be exports because not private fields are being accessed
	opens com.google.guice.jpms.example.impl to com.google.guice;
	//Opens because private fields are being accessed
	opens com.google.guice.jpms.example.internal to com.google.guice;


	//This module uses a service SPI
	uses IDownwardInjection;

	//This will provide a quickly built instance, but not one that guice built
	//This goes across modules and doesn't require an exports package to use
	provides IDownwardInjection with IDownwardInjectionImpl;
}
