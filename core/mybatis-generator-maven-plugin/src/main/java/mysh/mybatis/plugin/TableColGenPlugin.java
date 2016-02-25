package mysh.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * gen an inner class to each bean, which contains all defined columns.
 *
 * @author mysh
 * @since 2016/2/25
 */
public class TableColGenPlugin extends PluginAdapter {

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass clazz, IntrospectedTable table) {
		InnerClass colClass = new InnerClass(clazz.getType().getShortName() + "Cols");
		colClass.setVisibility(JavaVisibility.PUBLIC);
		colClass.setStatic(true);
		colClass.setFinal(true);
		clazz.addInnerClass(colClass);

		for (IntrospectedColumn col : table.getBaseColumns()) {
			Field field = new Field(col.getJavaProperty(), FullyQualifiedJavaType.getStringInstance());
			field.setVisibility(JavaVisibility.PUBLIC);
			field.setFinal(true);
			field.setInitializationString("\"" + col.getActualColumnName() + "\"");
			colClass.addField(field);
		}

		Field colField = new Field(colClass.getType().getShortName(), colClass.getType());
		colField.setVisibility(JavaVisibility.PUBLIC);
		colField.setStatic(true);
		colField.setFinal(true);
		colField.setInitializationString("new " + colField.getType().getShortName() + "()");
		clazz.addField(colField);
		return true;
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
}
