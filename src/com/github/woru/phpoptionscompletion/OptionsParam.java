package com.github.woru.phpoptionscompletion;


import java.util.Map;

public class OptionsParam {
    private final int position;
    private final Map<String, String> options;

    public OptionsParam(int position, Map<String, String> options) {
        this.position = position;
        this.options = options;
    }

    public int getPosition() {
        return position;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionsParam that = (OptionsParam) o;

        if (position != that.position) return false;
        if (!options.equals(that.options)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + options.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OptionsParam{" +
                "position=" + position +
                ", options=" + options +
                '}';
    }
}
