package com.jettra.report;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ReportUtils {

    public static Object getFieldValue(Object obj, String expression) {
        if (obj == null) return "";
        try {
            Field field = obj.getClass().getDeclaredField(expression);
            field.setAccessible(true);
            Object val = field.get(obj);
            
            // Check for @TableColumnField via reflection to avoid direct dependency
            for (java.lang.annotation.Annotation anno : field.getAnnotations()) {
                if (anno.annotationType().getSimpleName().equals("TableColumnField")) {
                    try {
                        Method m = anno.annotationType().getMethod("field");
                        String targetField = (String) m.invoke(anno);
                        if (targetField != null && !targetField.isEmpty()) {
                            if (val instanceof List) {
                                List<?> list = (List<?>) val;
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < list.size(); i++) {
                                    sb.append(getNestedFieldValue(list.get(i), targetField));
                                    if (i < list.size() - 1) sb.append(", ");
                                }
                                return sb.toString();
                            } else {
                                return getNestedFieldValue(val, targetField);
                            }
                        }
                    } catch (Exception e) {}
                }
            }
            return val;
        } catch (Exception e) {
            // Try getter if field not found
            try {
                String getterName = "get" + expression.substring(0, 1).toUpperCase() + expression.substring(1);
                Method m = obj.getClass().getMethod(getterName);
                return m.invoke(obj);
            } catch (Exception e2) {
                return expression;
            }
        }
    }

    private static Object getNestedFieldValue(Object obj, String expression) {
        if (obj == null) return "";
        try {
            Field field = obj.getClass().getDeclaredField(expression);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            try {
                String getterName = "get" + expression.substring(0, 1).toUpperCase() + expression.substring(1);
                Method m = obj.getClass().getMethod(getterName);
                return m.invoke(obj);
            } catch (Exception e2) {
                return obj.toString();
            }
        }
    }
}
