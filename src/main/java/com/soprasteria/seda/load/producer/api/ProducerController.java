package com.soprasteria.seda.load.producer.api;

import com.soprasteria.seda.load.producer.services.Producer;
import com.soprasteria.seda.load.producer.api.model.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping("/producer")
public class ProducerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerController.class);

	@Autowired
	private Producer producer;

	@RequestMapping(method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity startProducers(@RequestBody @Valid ProducerConfig config) {
		LOGGER.info("Start producer configuration -> {}", config);
		try {
			producer.run(config);
			return new ResponseEntity(HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}