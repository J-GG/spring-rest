package fr.jg.springrest.data.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A Filter intercepting the JSON response sent back to the client in order to only keep the requested fields.
 */
@Component
public class JsonFieldsFilter implements Filter {
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final String fieldsParam = request.getParameter("fields");

        if (fieldsParam != null) {
            final BodyResponseWrapper capturingResponseWrapper = new BodyResponseWrapper((HttpServletResponse) response);
            filterChain.doFilter(request, capturingResponseWrapper);

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode root = mapper.readTree(capturingResponseWrapper.getCaptureAsString());

            if (response.getContentType() != null && response.getContentType().contains("application/json")
                    && !(root.has("timestamp") && root.has("request") && root.has("response"))) {

                final List<String> fields = Arrays.asList(fieldsParam.split(","));
                final FieldNode fieldTree = new FieldNode("", "");

                fields.forEach(fullField -> {
                    final List<String> path = Arrays.asList(fullField.split("\\."));
                    fieldTree.parse(path);
                });

                fieldTree.asMap().forEach((parentPath, fieldNames) -> StreamSupport.stream(root.spliterator(), false)
                        .forEach(jsonNode -> {
                            final JsonNode parentNode = jsonNode.at(parentPath);
                            if (!parentNode.isEmpty()) {
                                ((ObjectNode) parentNode).retain(fieldNames);
                            }
                        }));
            }
            response.getWriter().write(mapper.writeValueAsString(root));
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public class FieldNode {

        private final String parent;

        private final String name;

        private final Set<FieldNode> children;

        public FieldNode(final String parent, final String name) {
            this.parent = parent;
            this.name = name;
            this.children = new HashSet<>();
        }

        public void parse(final List<String> path) {
            if (path != null && !path.isEmpty()) {
                this.children.stream()
                        .filter(fieldNode -> fieldNode.name.equals(path.get(0)))
                        .findAny()
                        .orElseGet(() -> {
                            final FieldNode child = new FieldNode(this.parent.concat(this.name).concat("/"), path.get(0));
                            this.children.add(child);
                            return child;
                        }).parse(path.subList(1, path.size()));
            }
        }

        public Map<String, List<String>> asMap() {
            final Map<String, List<String>> map = new HashMap<>();
            map.put(this.parent.concat(this.name), this.children.stream().map(FieldNode::getName).collect(Collectors.toList()));
            this.children.stream().filter(fieldNode -> !fieldNode.children.isEmpty()).forEach(fieldNode -> map.putAll(fieldNode.asMap()));

            return map;
        }

        public String getName() {
            return this.name;
        }
    }
}

