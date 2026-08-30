package org.example.dataprocessor.loader;

import org.example.model.Measurement;

import java.util.List;

public interface Loader {

    List<Measurement> load();

}
