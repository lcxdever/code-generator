package com.roc.generator.unittest.model;

import com.roc.generator.javadoc.model.ControllerMethodMd;
import com.roc.generator.util.ReflectTool;
import com.roc.generator.util.StringTool;
import org.junit.Test;

import java.util.Date;

/**
 * 用于测试各种代码
 *
 * @author yuman @date 2022-02-14
 */
public class MainTest {

    @Test
    public void test() {
//        TypeInfo typeInfo = TypeInfo.fromNameGenericsCanonical("java.List<c.Create<a.Test, c.Just>>");
//        System.out.println(typeInfo.getNameGenericsSimple());
//        Tmp tmp = new Tmp();
//        tmp.setDate(new Date());
//        System.out.println(GsonUtil.prettyJson(tmp));

        ReflectTool.getClassFields(ControllerMethodMd.class)
                .forEach(e -> System.out.println(StringTool.camelToUnderline(e.getName())));
    }



    public static class Tmp {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    @Test
    public void testPlugin() throws Exception {
    }
}