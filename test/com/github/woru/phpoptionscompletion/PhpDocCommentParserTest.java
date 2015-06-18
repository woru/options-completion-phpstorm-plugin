package com.github.woru.phpoptionscompletion;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PhpDocCommentParserTest {
    @Test
    public void parseSingleOptionsParameter() {
        String comment = " Initializes this class with the given options.\n" +
                " \n" +
                "  @param array $options {\n" +
                "      @var bool   $required Whether this element is required\n" +
                "      @var string $label    The display name for this element\n" +
                "  }";

        Map<Integer, OptionsParam> optionsParams = new PhpDocCommentParser().parse(comment);

        assertEquals(1, optionsParams.size());
        assertEquals(new OptionsParam(0, ImmutableMap.of("required", "bool", "label", "string")), optionsParams.get(0));
    }

    @Test
    public void parseMultipleOptionsParameter() {
        String comment = " Initializes this class with the given options.\n" +
                "  @param mixed[] $param1 Array structure to count the elements of. \n" +
                "  @param array $options1 {\n" +
                "      @var bool   $required Whether this element is required\n" +
                "      @var string $label    The display name for this element\n" +
                "  } \n" +
                "  @param string $param2 Array structure to count the elements of. \n" +
                "  @param array $options2 {\n" +
                "      @var int   $size Whether this element is required\n" +
                "      @var string $name The display name for this element\n" +
                "  }";

        Map<Integer, OptionsParam> optionsParams = new PhpDocCommentParser().parse(comment);

        assertEquals(2, optionsParams.size());
        assertEquals(new OptionsParam(1, ImmutableMap.of("required", "bool", "label", "string")), optionsParams.get(1));
        assertEquals(new OptionsParam(3, ImmutableMap.of("size", "int", "name", "string")), optionsParams.get(3));
    }

}