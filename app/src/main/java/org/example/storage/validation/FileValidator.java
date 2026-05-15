package org.example.storage.validation;

import org.example.domain.Measurement;
import org.example.domain.Protocol;
import org.example.domain.Sample;
import org.example.validator.MeasurementValidator;
import org.example.validator.ProtocolValidator;
import org.example.validator.SampleValidator;

import java.util.*;

public class FileValidator {

    public static List<String> validate(List<Sample> samples,
                                        List<Measurement> measurements,
                                        List<Protocol> protocols) {
        List<String> errors = new ArrayList<>();

        Set<Long> sampleIds = new HashSet<>();
        for (Sample s : samples) {
            if (!sampleIds.add(s.getId())) {
                errors.add("Duplicate sample id=" + s.getId());
            }
        }
        Set<Long> measurementIds = new HashSet<>();
        for (Measurement m : measurements) {
            if (!measurementIds.add(m.getId())) {
                errors.add("Duplicate measurement id=" + m.getId());
            }
        }
        Set<Long> protocolIds = new HashSet<>();
        for (Protocol p : protocols) {
            if (!protocolIds.add(p.getId())) {
                errors.add("Duplicate protocol id=" + p.getId());
            }
        }

        for (Sample s : samples) {
            try {
                SampleValidator.validateName(s.getName());
                SampleValidator.validateType(s.getType());
                SampleValidator.validateLocation(s.getLocation());
                SampleValidator.validateOwnerUsername(s.getOwnerUsername());
            } catch (IllegalArgumentException e) {
                errors.add("Sample id=" + s.getId() + ": " + e.getMessage());
            }
        }

        for (Measurement m : measurements) {
            try {
                MeasurementValidator.validateParam(m.getParam());
                MeasurementValidator.validateValue(m.getValue());
                MeasurementValidator.validateUnit(m.getUnit());
                MeasurementValidator.validateMethod(m.getMethod());
                MeasurementValidator.validateOwnerUsername(m.getOwnerUsername());
            } catch (IllegalArgumentException e) {
                errors.add("Measurement id=" + m.getId() + ": " + e.getMessage());
            }
            if (!sampleIds.contains(m.getSampleId())) {
                errors.add("Measurement id=" + m.getId() + ": sampleId="
                        + m.getSampleId() + " ссылается на несуществующий sample");
            }
        }

        for (Protocol p : protocols) {
            try {
                ProtocolValidator.validateName(p.getName());
                ProtocolValidator.validateRequiredParams(p.getRequiredParams());
                ProtocolValidator.validateOwnerUsername(p.getOwnerUsername());
            } catch (IllegalArgumentException e) {
                errors.add("Protocol id=" + p.getId() + ": " + e.getMessage());
            }
        }

        return errors;
    }
}