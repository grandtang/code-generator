package cn.tangzy.cg;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tangzhiyuan@bitnei.cn
 * @date 8/22/18
 **/
public class GeneratorMain {

    public static void main(String[] args) throws Exception {
        String directory = "/home/tangzy/workspace/github/code-generator";
        String projectName = "ras";
        String basePackage = "cn.bit.bdp.ras";
        String jdbcConnection = "jdbc:mysql://localhost:3306/ras_test";
        String username = "admin";
        String password = "root";

        MybatisCodeGeneratorHelper helper = new MybatisCodeGeneratorHelper(directory, basePackage, projectName, jdbcConnection, username, password);

        helper.generate("PERMISSION", "Permission", "permission");
        helper.generate("RESOURCE", "Resource", "resource");
        helper.generate("ROLE", "Role", "role");
        helper.generate("ROLE_PERMISSION", "RolePermission", "role");
        helper.generate("TENANT", "Tenant", "tenant");
        helper.generate("USER", "User", "user");
        helper.generate("USER_ATTR", "UserAttr", "user");
        helper.generate("USER_PERMISSION", "UserPermission", "user");
        helper.generate("USER_ROLE","UserRole","user");
        helper.generate("USER_TOKEN","UserToken","user");
        helper.generate("RESOURCE_RELATION","ResourceRelation","resource");

    }

    private static void generatorByConfig(String fileName) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        InputStream resourceAsStream = GeneratorMain.class.getClassLoader().getResourceAsStream(fileName);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(resourceAsStream);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }


}
