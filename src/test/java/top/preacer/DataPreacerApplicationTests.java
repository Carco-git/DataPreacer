package top.preacer;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataPreacerApplicationTests {

	@Test
	void contextLoads() {
		List<String> a = new ArrayList<String>();
		a.add("a");
		a.add(null);
		System.out.println(a);
	}

}
