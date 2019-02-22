package com.search;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.text.html.Option;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.agileengine.JsoupCssSelectSnippet;
import com.agileengine.JsoupFindByIdSnippet;

@SpringBootApplication
public class App implements CommandLineRunner {
	public static void main(String[] args) {

		System.out.println(System.getProperty("java.class.path"));
		SpringApplication.run(App.class, args);

	}

	@Override
	public void run(String... args) throws Exception {

		Logger LOGGER = LoggerFactory.getLogger(App.class);
		String path = "/Users/iguanafix/agileEngineSearch/sample-0-origin.html";
		String test1 = "/Users/iguanafix/agileEngineSearch/sample-1-evil-gemini.html";
		String targetElementId = "make-everything-ok-button";

		Optional<Element> buttonOpt = JsoupFindByIdSnippet.findElementById(new File(path), targetElementId);

		List<Attribute> attributeList = buttonOpt.get().attributes().asList();

		Map<Element, Integer> elementsIntegerMap = new HashMap<>();

		attributeList.stream().forEach(attribute -> {

			Optional<Elements> elements = JsoupCssSelectSnippet
					.findElementsByQuery(new File(test1), "a[" + attribute.getKey() + "=\"" + attribute.getValue()+"\"]");

			elements.get().stream().forEach(element -> {
				if (elementsIntegerMap.containsKey(element)) {
					elementsIntegerMap.put(element, elementsIntegerMap.get(element) + 1);
				} else {
					elementsIntegerMap.put(element, 1);
				}
			});

		});

		Map<Element, Integer> orderedElements = elementsIntegerMap.entrySet().stream()
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		Map.Entry<Element, Integer> entry = orderedElements.entrySet().iterator().next();
		Element element = entry.getKey();

		LOGGER.info("ELement:" +
				element.attributes().asList().stream().map(attribute -> attribute.getKey() + "=" + attribute.getValue())
						.collect(Collectors.joining(",")));
		//		Optional<String> stringifiedAttributesOpt = buttonOpt.map(button -> button.attributes().asList().stream()
		//				.map(attr -> attr.getKey() + " = " + attr.getValue()).collect(Collectors.joining(", ")));
		//
		//		stringifiedAttributesOpt.ifPresent(attrs -> LOGGER.info("Target element attrs: [{}]", attrs));
	}
}

