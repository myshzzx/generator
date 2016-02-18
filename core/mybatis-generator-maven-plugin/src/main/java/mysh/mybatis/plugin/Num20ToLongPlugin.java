package mysh.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Num19ToLongPlugin
 *
 * @author mysh
 * @since 2016/1/29
 */
public class Num20ToLongPlugin extends PluginAdapter {
	private static final FullyQualifiedJavaType bigDecimalType = new FullyQualifiedJavaType(BigDecimal.class.getName());
	private static final FullyQualifiedJavaType longType = new FullyQualifiedJavaType(Long.class.getName());

	@Override
	public void initialized(IntrospectedTable table) {
		List<IntrospectedColumn> cols = table.getAllColumns();
		for (IntrospectedColumn col : cols) {
			if (col.getLength() < 21 && bigDecimalType.equals(col.getFullyQualifiedJavaType())) {
				col.setFullyQualifiedJavaType(longType);
			}
		}
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
}
