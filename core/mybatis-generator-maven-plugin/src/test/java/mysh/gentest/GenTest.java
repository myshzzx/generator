package mysh.gentest;

import org.junit.Test;
import org.mybatis.generator.api.ShellRunner;

import java.io.IOException;

/**
 * GenTest
 *
 * @author mysh
 * @since 2016/2/18
 */
public class GenTest {
	@Test
	public void genMybatisBean() throws IOException, ClassNotFoundException {
		String file = "D:\\OtherProj\\mybatis-generator\\core\\mybatis-generator-maven-plugin\\src\\test\\resources\\mybatis-gen.xml";
		ShellRunner.main(new String[]{"-configfile", file, "-overwrite"});
	}

}
