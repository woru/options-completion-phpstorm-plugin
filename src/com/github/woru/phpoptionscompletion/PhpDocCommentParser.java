package com.github.woru.phpoptionscompletion;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhpDocCommentParser {
    private static final Pattern paramPattern = Pattern.compile("(?:@param\\s+array\\s+\\$\\w+\\s+\\{([^}]+)\\})|(?:@param[^}\n]+)");
    private static final Pattern optionPattern = Pattern.compile("@(?:var|type)\\s+(\\w+(?:\\|\\w+)*)\\s+\\$(\\w+)[^\n]*");

    public Map<Integer, OptionsParam> parse(String comment) {
        int position = 0;
        Map<Integer, OptionsParam> optionsParams = new HashMap<Integer, OptionsParam>();
        Matcher matcher = paramPattern.matcher(comment);
        while (matcher.find()) {
            String optionsString = matcher.group(1);
            Map<String, String> options = parseOptions(optionsString);
            if (!options.isEmpty()) {
                optionsParams.put(position, new OptionsParam(position, options));
            }
            position++;
        }
        return optionsParams;
    }

    private Map<String, String> parseOptions(String optionsString) {
        Map<String, String> options = new HashMap<String, String>();
        if (optionsString == null) {
            return options;
        }
        Matcher optionsMatcher = optionPattern.matcher(optionsString);

        while (optionsMatcher.find()) {
            String type = optionsMatcher.group(1);
            String name = optionsMatcher.group(2);
            options.put(name, type);
        }
        return options;
    }
}
