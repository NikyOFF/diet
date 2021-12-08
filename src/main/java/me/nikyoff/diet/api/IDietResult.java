package me.nikyoff.diet.api;

import java.util.Map;

public interface IDietResult {
    Map<IDietGroup, Float> get();
}
