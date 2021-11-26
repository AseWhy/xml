package io.github.asewhy.xml.support.interfaces;

public class CommonBuilderWriter implements iWriter {
    private final StringBuilder builder;

    public CommonBuilderWriter(StringBuilder stream) {
        this.builder = stream;
    }

    @Override
    public CommonBuilderWriter write(String chars) {
        builder.append(chars); return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
