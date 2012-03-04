package com.gmail.takashi316.acousticpositioning;

/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.api.client.util.Key;

import java.util.List;

/**
 * @author Yaniv Inbar
 */
public class DocumentLink {

	@Key("@href")
	public String href;

	@Key("@rel")
	public String rel;

	public static String find(List<DocumentLink> document_link_list, String rel) {
		if (document_link_list != null) {
			for (DocumentLink link : document_link_list) {
				if (rel.equals(link.rel)) {
					return link.href;
				}
			}
		}
		return null;
	}// find
}// DocumentLink