package fr.jg.springrest.data.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Filter intercepting the JSON response sent back to the client in order to only keep the requested fields.
 */
@Component
public class PagedResponseFilter implements Filter {
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final String fieldsParam = request.getParameter("fields");
        final String groupByParam = request.getParameter("groupBy");

        final Set<String> fields = new HashSet<>();
        if (fieldsParam != null) {
            fields.addAll(Arrays.asList(fieldsParam.split(",")));
        }
        if (groupByParam != null) {
            fields.addAll(Arrays.asList(groupByParam.split(",")));
        }

        final BodyResponseWrapper capturingResponseWrapper = new BodyResponseWrapper((HttpServletResponse) response);
        filterChain.doFilter(request, capturingResponseWrapper);

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(capturingResponseWrapper.getCaptureAsString());

        final JsonNode newRoot = mapper.createArrayNode();
        root.forEach(jsonNode -> {
            if (jsonNode.has("aggregates") && jsonNode.has("resource")) {
                final ObjectNode aggregates = (ObjectNode) jsonNode.get("aggregates");
                final ObjectNode newJsonNode = (ObjectNode) jsonNode.get("resource");
                newJsonNode.setAll(aggregates);
                ((ArrayNode) newRoot).add(newJsonNode);
                aggregates.fieldNames().forEachRemaining(fields::add);
            }
        });

        if (fieldsParam != null || groupByParam != null) {
            if (response.getContentType() != null && response.getContentType().contains("application/json")
                    && !(newRoot.has("timestamp") && newRoot.has("request") && newRoot.has("response"))) {

                final FieldNode fieldTree = new FieldNode(fields);

                fieldTree.pruneJsonNode(newRoot);
            }
            response.getWriter().write(mapper.writeValueAsString(newRoot));
        } else {
            response.getWriter().write(mapper.writeValueAsString(newRoot));
        }
    }

    /**
     * Used to represent the fields path as a tree.
     * Each node is a field of a class.
     */
    public static class FieldNode {
        /**
         * Name of the field.
         */
        private final String name;

        /**
         * The list of child nodes.
         */
        private final Set<FieldNode> children;

        /**
         * Constructor.
         *
         * @param fields The list of fields which should be stored in the tree.
         */
        public FieldNode(final Set<String> fields) {
            this.name = "";
            this.children = new HashSet<>();

            fields.forEach(fullField -> {
                final List<String> path = Arrays.asList(fullField.split("\\."));
                this.parse(path);
            });
        }

        /**
         * Constructor.
         *
         * @param name The name of the field.
         */
        private FieldNode(final String name) {
            this.name = name;
            this.children = new HashSet<>();
        }

        /**
         * Adds recursively the fields contained in the path to the children.
         *
         * @param path The path to parse.
         */
        public void parse(final List<String> path) {
            if (path != null && !path.isEmpty()) {
                this.children.stream()
                        .filter(fieldNode -> fieldNode.name.equals(path.get(0)))
                        .findAny()
                        .orElseGet(() -> {
                            final FieldNode child = new FieldNode(path.get(0));
                            this.children.add(child);
                            return child;
                        }).parse(path.subList(1, path.size()));
            }
        }

        /**
         * Removes the fields from the JsonNode which are not in the collection of children.
         *
         * @param jsonNode The node to prune.
         */
        public void pruneJsonNode(final JsonNode jsonNode) {
            if (jsonNode.isArray()) {
                jsonNode.forEach(this::pruneJsonNode);
            } else {
                if (!jsonNode.isNull()) {
                    ((ObjectNode) jsonNode).retain(this.children.stream().map(FieldNode::getName).collect(Collectors.toList()));
                    this.children.stream().filter(fieldNode -> !fieldNode.children.isEmpty()).forEach(fieldNode ->
                            fieldNode.pruneJsonNode(jsonNode.at("/".concat(fieldNode.getName())))
                    );
                }
            }
        }

        /**
         * Gets the name of the field.
         *
         * @return The name of the field.
         */
        public String getName() {
            return this.name;
        }
    }
}

