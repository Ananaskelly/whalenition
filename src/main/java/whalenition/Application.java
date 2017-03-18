package whalenition;

import org.opencv.core.Core;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import recognition.MLP;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		MLP.init(3, new int[]{28*28,80,10});
		MLP.setTANH();
		MLP.loadWeights();
		SpringApplication.run(Application.class, args);
	}
	@Bean
	CommandLineRunner init(FilesStorage storage) {
		return (args) -> {
			storage.init();
		};
	}
}
