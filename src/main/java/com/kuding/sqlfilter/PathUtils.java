package com.kuding.sqlfilter;

import javax.persistence.criteria.Path;

import org.apache.commons.lang3.StringUtils;

import com.kuding.exceptions.JpaAmebaException;

public class PathUtils {

	public static Path<?> getPath(String pahtName, Path<?> path) {
		if (StringUtils.isBlank(pahtName))
			throw new JpaAmebaException("不存在的字段信息");
		for (String speratedField : pahtName.split("\\.")) {
			if (StringUtils.isBlank(speratedField))
				throw new JpaAmebaException("字段信息不正确");
			path = path.get(speratedField);
		}
		return path;
	}

}
