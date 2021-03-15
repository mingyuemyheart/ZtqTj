package com.pcs.ztqtj.model.pack;

import android.content.res.Resources.NotFoundException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * 交通气象
 * 
 * @author JiangZy
 * 
 */
public class PackLocalTrafficWeather {

	public Map<String, TrafficHighWay> map = new HashMap<String, TrafficHighWay>();

	public void fillData(InputSource source) {
		map.clear();

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		XMLReader reader;
		try {
			reader = factory.newSAXParser().getXMLReader();
			reader.setContentHandler(new MyHandler());
			reader.parse(source);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class MyHandler extends DefaultHandler {

		private final String ID = "ID";
		private final String NAME = "NAME";
		private final String SEARCH_NAME = "SEARCH_NAME";
		private final String SHOW_LATITUDE = "SHOW_LATITUDE";
		private final String SHOW_LONGITUDE = "SHOW_LONGITUDE";
		private final String IMAGE_PATH = "IMAGE_PATH";
		private final String DETAIL_IMAGE = "DETAIL_IMAGE";
		private final String ELEMENT = "ROW";

		private String tag;
		private String id;
		private String name;
		private double show_latitude;
		private double show_longitude;
		private String search_name;
		private String image_path;
		private String detail_image;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			tag = localName;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (tag.equals(ID)) {
				id = new String(ch, start, length);
			} else if (tag.equals(NAME)) {
				name = new String(ch, start, length);
			} else if (tag.equals(SEARCH_NAME)) {
				search_name = new String(ch, start, length);
			} else if (tag.equals(SHOW_LATITUDE)) {
				String str = new String(ch, start, length);
				show_latitude = Double.valueOf(str);
			} else if (tag.equals(SHOW_LONGITUDE)) {
				show_longitude = Double.valueOf(new String(ch, start, length));
			} else if (tag.equals(IMAGE_PATH)) {
				image_path = new String(ch, start, length);
			} else if (tag.equals(DETAIL_IMAGE)) {
				detail_image = new String(ch, start, length);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			tag = "";
			if (localName.equals(ELEMENT)) {
				TrafficHighWay pack = new TrafficHighWay();
				pack.ID = id;
				pack.NAME = name;
				pack.SEARCH_NAME = search_name;
				pack.SHOW_LATITUDE = show_latitude;
				pack.SHOW_LONGITUDE = show_longitude;
				pack.IMAGE_PATH = image_path;
				pack.DETAIL_IMAGE = detail_image;
				map.put(id, pack);

				id = "";
				name = "";
				search_name = "";
				show_latitude = 0;
				show_longitude = 0;
				image_path = "";
				detail_image = "";
			}
		}
	}

	/**
	 * 根据search_name获取item
	 * 
	 * @param search_name
	 * @return
	 */
	public TrafficHighWay getItemBySearchName(String search_name) {
		Iterator<Entry<String, TrafficHighWay>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, TrafficHighWay> pairs = (Entry<String, TrafficHighWay>) it
					.next();
			TrafficHighWay pack = pairs.getValue();
			if (pack.SEARCH_NAME.equals(search_name)) {
				return pack;
			}
		}

		return null;
	}
}
