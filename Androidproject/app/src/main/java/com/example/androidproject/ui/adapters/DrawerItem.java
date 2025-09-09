package com.example.androidproject.ui.adapters;

import com.example.androidproject.data.models.Label;

import java.util.ArrayList;
import java.util.List;

public abstract class DrawerItem {
    public enum Type {HEADER, SECTION,LABEL}
    public abstract Type getType();

    public static final class HeaderItem extends DrawerItem {
        @Override
        public Type getType() {
            return Type.HEADER;
        }
    }
    public static final class SectionItem extends DrawerItem {
        @Override
        public Type getType() {
            return Type.SECTION;
        }
    }
    public static final class LabelItem extends DrawerItem {
        public Label label;
        public LabelItem(Label label) {
            this.label = label;
        }
        @Override
        public Type getType() {
            return Type.LABEL;
        }
    }
    public static List<DrawerItem> buildDrawerItems(
        HeaderItem header,
        List<LabelItem> systemLabels,
        SectionItem section,
        List<LabelItem> userLabels
    ) {
        List<DrawerItem> items = new ArrayList<>();
        if (header != null) {
            items.add(header);
        }
        if (systemLabels != null && !systemLabels.isEmpty()) {
            items.addAll(systemLabels);
        }
        if (section != null) {
            items.add(section);
        }
        if (userLabels != null && !userLabels.isEmpty()) {
            items.addAll(userLabels);
        }
        return items;
    }
}
