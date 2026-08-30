package org.example.dataprocessor;

import org.example.model.Measurement;

import java.util.List;

public interface Loader {

    List<Measurement> load();

}
