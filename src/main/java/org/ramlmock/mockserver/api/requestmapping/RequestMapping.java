/**
 * Copyright © 2016 Ariel Segura (ariel.segura@yahoo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ramlmock.mockserver.api.requestmapping;

import org.ramlmock.mockserver.internal.ResourceMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by arielsegura on 12/8/16.
 */
public class RequestMapping {

    private Map<String, ResourceMap> resources = new HashMap<>();

    public Optional<ResourceMap> getResource(String name){
        return Optional.ofNullable(resources.get(name));
    }

    public void addResource(String resource, ResourceMap resourceMap) {
        resources.put(resource, resourceMap);
    }
}
