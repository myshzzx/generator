package mysh.mybatis.plugin;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellRunner;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Properties;

public class PaginationPlugin extends PluginAdapter {
	private String dbType = "mysql";

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);

		String dbTypeProp = getContext().getProperty("DBType");
		if (dbTypeProp != null)
			this.dbType = dbTypeProp.toLowerCase();
	}

	private boolean isOracleDB() {
		return "oracle".equals(dbType);
	}

	private boolean isMysqlDB() {
		return "mysql".equals(dbType);
	}

	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
					XmlElement element, IntrospectedTable introspectedTable) {
//		if (isMysqlDB())
//			xmlPageMysql(element);
//		else if (isOracleDB())
//			xmlPageOracle(element);
		xmlPage(element);
		return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
	}

	private void xmlPage(XmlElement element){
		XmlElement headEle = new XmlElement("include");
		headEle.addAttribute(new Attribute("refid", "pageMapper.head"));
		element.addElement(0, headEle);

		XmlElement tailEle = new XmlElement("include");
		tailEle.addAttribute(new Attribute("refid", "pageMapper.tail"));
		element.addElement(tailEle);
	}
	
	private void xmlPageOracle(XmlElement element) {
		XmlElement headEle = new XmlElement("if"); 
		headEle.addAttribute(new Attribute("test", "limitStart != null and limitStart >=0"));
		headEle.addElement(new TextElement("SELECT * FROM ( SELECT A_.*, ROWNUM RN FROM  ("));
		element.addElement(0, headEle);

		XmlElement tailEle = new XmlElement("if");
		tailEle.addAttribute(new Attribute("test", "limitStart != null and limitStart >=0"));
		tailEle.addElement(new TextElement(") A_ WHERE ROWNUM &lt;= #{limitEnd}) WHERE RN &gt; #{limitStart} "));
		element.addElement(tailEle);
	}

	private void xmlPageMysql(XmlElement element) {
		// LIMIT5,10; // 检索记录行 6-15
		XmlElement isNotNullElement = new XmlElement("if");
		isNotNullElement.addAttribute(new Attribute("test", "limitStart != null and limitStart >=0"));
		isNotNullElement.addElement(new TextElement("limit ${limitStart}, ${limitPageSize}"));
		element.addElement(isNotNullElement);
	}

	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// add field, getter, setter for limit clause
		addLimit(topLevelClass, introspectedTable, "limitStart");
		addLimit(topLevelClass, introspectedTable, "limitEnd");
		addLimit(topLevelClass, introspectedTable, "limitPageSize");

		Method pageMethod = new Method();
		pageMethod.setVisibility(JavaVisibility.PUBLIC);
		pageMethod.setReturnType(topLevelClass.getType());
		pageMethod.setName("page");
		pageMethod.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "pageNo"));
		pageMethod.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "pageSize"));

		pageMethod.addBodyLine("this.limitStart= (pageNo-1) * pageSize;");
		pageMethod.addBodyLine("this.limitEnd = pageNo * pageSize;");
		pageMethod.addBodyLine("this.limitPageSize = pageSize;");
		pageMethod.addBodyLine("return this;");
		topLevelClass.addMethod(pageMethod);

		return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
	}

	private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Field field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getIntInstance());
		field.setName(name);
		field.setInitializationString("-1");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		char c = name.charAt(0);
		String camel = Character.toUpperCase(c) + name.substring(1);

		Method setMethod = new Method();
		setMethod.setVisibility(JavaVisibility.PUBLIC);
		setMethod.setName("set" + camel);
		setMethod.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name));
		setMethod.addBodyLine("this." + name + "=" + name + ";");
		commentGenerator.addGeneralMethodComment(setMethod, introspectedTable);
		topLevelClass.addMethod(setMethod);

		Method getMethod = new Method();
		getMethod.setVisibility(JavaVisibility.PUBLIC);
		getMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
		getMethod.setName("get" + camel);
		getMethod.addBodyLine("return " + name + ";");
		commentGenerator.addGeneralMethodComment(getMethod, introspectedTable);
		topLevelClass.addMethod(getMethod);
	}

	/**
	 * This plugin is always valid -no properties are required
	 */
	public boolean validate(List<String> warnings) {
		return true;
	}

	public static void main(String[] args) {
		String config = PaginationPlugin.class.getClassLoader().getResource("mybatisConfig.xml").getFile();
		String[] arg = {"-configfile", config, "-overwrite"};
		ShellRunner.main(arg);
	}
}
