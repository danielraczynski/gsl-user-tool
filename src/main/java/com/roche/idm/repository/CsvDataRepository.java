package com.roche.idm.repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.roche.idm.model.Group;
import com.roche.idm.model.User;

@Component
public class CsvDataRepository {

	private final String fileName = "users.txt";

	private final Logger logger = LoggerFactory.getLogger(CsvDataRepository.class);

	private <T> List<T> loadObjectList(Class<T> type, String fileName) {
		try {
			CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			File file;
			file = new ClassPathResource(fileName).getFile();
			MappingIterator<T> readValues = mapper.readerFor(type).with(bootstrapSchema).readValues(file);
			return readValues.readAll();
		} catch (IOException e) {
			logger.error("Error occurred while loading object list from file {}", fileName, e);
			return Collections.emptyList();
		}
	}

	public List<User> loadUsers() {
		return loadObjectList(User.class, fileName);
	}

	/**
	 * Save objects list
	 */
	public <T> void saveObjectList(Class<T> type, List<T> list) {
		try {
			CsvSchema bootstrapSchema = CsvSchema.builder()
					.addColumn("firstName")
					.addColumn("lastName")
					.addColumn("username")
					.addColumn("email")
					.build().withHeader();
			CsvMapper mapper = new CsvMapper();
			mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
			File file = new ClassPathResource(fileName).getFile();
			SequenceWriter writer = mapper.writerFor(type).with(bootstrapSchema).writeValues(file);
			for (T t : list) {
				writer.write(t);
			}
			writer.flush();
		} catch (IOException e) {
			logger.error("Cannot save to file {}", fileName, e);
		}
	}
}
