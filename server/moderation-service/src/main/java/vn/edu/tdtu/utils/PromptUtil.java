package vn.edu.tdtu.utils;

import java.util.Map;

public class PromptUtil {
    public static String create(String stringTemplate, Map<String, String> context) {
        if (stringTemplate == null) {
            return "";
        }

        if(context == null) {
            return stringTemplate;
        }

        for (Map.Entry<String, String> entry : context.entrySet()) {
            String placeholder = "\\{" + entry.getKey() + "\\}";
            stringTemplate = stringTemplate.replaceAll(placeholder, entry.getValue());
        }

        return stringTemplate;
    }
}
