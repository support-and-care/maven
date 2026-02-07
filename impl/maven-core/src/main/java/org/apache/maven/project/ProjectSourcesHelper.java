/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.project;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.maven.api.model.Source;

/**
 * Utility methods for analyzing the {@code <source>} elements of a project.
 * <strong>Warning:</strong> This is an internal utility class that is only public for technical reasons.
 * It is not part of the public <abbr>API</abbr>. In particular, this class can be changed or deleted without
 * prior notice.
 */
public class ProjectSourcesHelper {
    /**
     * All sources of the project.
     */
    protected final Collection<Source> sources;

    /**
     * Creates a new helper for the given project.
     *
     * @param project the Maven project from which to get the sources
     */
    public ProjectSourcesHelper(final MavenProject project) {
        sources = project.getBuild().getDelegate().getSources();
    }

    /**
     * Returns whether the project declares at least one {@code <source>} element which is enabled.
     * This is regardless if the source declares a module or not.
     *
     * @return whether the project declares at least one {@code <source>} element which is enabled
     */
    public boolean hasEnabledSources() {
        for (Source source : sources) {
            if (source.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a stream of non-blank module names.
     * The stream may contain duplicated values.
     * This method does not filter disabled sources.
     *
     * @return a stream of non-blank module names
     */
    private Stream<String> streamOfModuleNames() {
        return sources.stream()
                .map(org.apache.maven.api.model.Source::getModule)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank());
    }

    /**
     * Extracts unique module names from the list of source elements.
     * A project uses module source hierarchy if it has at least one module name.
     *
     * @return set of non-blank module names in declaration order
     */
    public Set<String> getModuleNames() {
        var modules = new LinkedHashSet<String>(); // Preferred to `Collectors.toSet()` for preserving order.
        streamOfModuleNames().forEach(modules::add);
        return modules;
    }

    /**
     * Whether the project uses module source hierarchy. This method returns {@code true} it at least one
     * {@code <source>} element declares a Java modules. While modular and non-modular sources should not be mixed,
     * this code is tolerant to such mixes because non-modular source elements may have been incorrectly generated
     * by non module-aware codes.
     *
     * @return whether the project uses module source hierarchy
     */
    public boolean useModuleSourceHierarchy() {
        return streamOfModuleNames().findAny().isPresent();
    }
}
