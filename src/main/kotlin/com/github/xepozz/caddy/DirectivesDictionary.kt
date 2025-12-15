package com.github.xepozz.caddy

import com.github.xepozz.caddy.documentation.CaddyDirectiveDoc

object DirectivesDictionary {
    val directives = mapOf(
        "abort" to CaddyDirectiveDoc(
            name = "abort",
            description = "Aborts the HTTP request with a specific error message and status code.",
            syntax = "abort [<status_code> [<message>]]",
            examples = """
                # Abort with 403 Forbidden
                abort 403 "You are not allowed to access this resource"

                # Simple abort with default 400 Bad Request
                abort
            """.trimIndent(),
        ),
        "acme_server" to CaddyDirectiveDoc(
            name = "acme_server",
            description = "An embedded ACME server that implements the ACME protocol. This allows Caddy to act as a CA within a private PKI.",
            syntax = "acme_server [<directory_url>] {\n\t...\n}",
            examples = """
                acme_server {
                    ca {
                        root {
                            cert cert.pem
                            key key.pem
                        }
                    }
                }
            """.trimIndent(),
        ),
        "basicauth" to CaddyDirectiveDoc(
            name = "basicauth",
            description = "Enables HTTP Basic Authentication. It can verify credentials against a hash or a .htpasswd file.",
            syntax = "basicauth [<matcher>] [<hash_algorithm> [<realm>]] {\n\t<username> <hashed_password_base64>\n\t...\n}",
            examples = """
                basicauth {
                    user JDJhJDEwJDFqZzYxTXE4MnRwRTJtMzVRTjhzT3VpYUxXWGN3cHZMUDdVeHpQL0xaYnIwZUxjZDhVUXVh
                }
            """.trimIndent(),
        ),
        "bind" to CaddyDirectiveDoc(
            name = "bind",
            description = "Customizes the server's socket address. By default, Caddy listens on all interfaces.",
            syntax = "bind <addresses...>",
            examples = """
                # Bind to a specific IP
                bind 192.168.1.2

                # Bind to multiple IPs
                bind 127.0.0.1 192.168.1.2
            """.trimIndent(),
        ),
        "encode" to CaddyDirectiveDoc(
            name = "encode",
            description = "Encodes responses using the specified encoding(s). Supports gzip, zstd, and Brotli compression.",
            syntax = "encode [<matcher>] [<formats...>] {\n\t[gzip [<level>]]\n\t[zstd [<level>]]\n\t[br [<quality>]]\n}",
            examples = """
                encode gzip zstd

                encode {
                    gzip 5
                    zstd 3
                }
            """.trimIndent(),
        ),
        "handle" to CaddyDirectiveDoc(
            name = "handle",
            description = "Groups directives that apply to the same requests. Useful for organizing your config.",
            syntax = "handle [<matcher>] {\n\t<directives...>\n}",
            examples = """
                handle /api/* {
                    reverse_proxy localhost:9000
                }

                handle {
                    root * /var/www/html
                    file_server
                }
            """.trimIndent(),
        ),
        "header" to CaddyDirectiveDoc(
            name = "header",
            description = "Manipulates HTTP headers. Can add, set, or delete headers from requests or responses.",
            syntax = "header [<matcher>] [[+|-]<field> [<value>...]]... {\n\t[+|-]<field> [<value>...]\n\t...\n}",
            examples = """
                # Add a header
                header +X-Custom-Header "My Value"

                # Remove a header
                header -Server

                # Set multiple headers
                header {
                    +X-Frame-Options "DENY"
                    +X-Content-Type-Options "nosniff"
                    -Server
                }
            """.trimIndent(),
        ),
        "log" to CaddyDirectiveDoc(
            name = "log",
            description = "Configures HTTP request logging. Supports various output formats and destinations.",
            syntax = "log [<matcher>] {\n\toutput <writer_module> ...\n\tformat <encoder_module> ...\n\t...\n}",
            examples = """
                log {
                    output file /var/log/access.log
                    format json
                }
            """.trimIndent(),
        ),
        "php_fastcgi" to CaddyDirectiveDoc(
            name = "php_fastcgi",
            description = "Serves PHP sites over FastCGI. Includes PHP-specific optimizations and error handling.",
            syntax = "php_fastcgi [<matcher>] <php_gateway> [<options>...]",
            examples = """
                php_fastcgi localhost:9000

                php_fastcgi unix//var/run/php-fpm.sock {
                    root /var/www/html
                    split .php
                    env PHP_VALUE "memory_limit=128M"
                }
            """.trimIndent(),
        ),
        "reverse_proxy" to CaddyDirectiveDoc(
            name = "reverse_proxy",
            description = "Proxies requests to one or more backends with load balancing, health checking, and more.",
            syntax = "reverse_proxy [<matcher>] [<upstreams...>] {\n\t<options...>\n}",
            examples = """
                # Simple reverse proxy
                reverse_proxy localhost:9000

                # Load balancing with health checks
                reverse_proxy app1:80 app2:80 {
                    lb_policy round_robin
                    health_path /health
                    health_interval 10s
                }
            """.trimIndent(),
        ),
        "rewrite" to CaddyDirectiveDoc(
            name = "rewrite",
            description = "Rewrites the request URI internally. The client's URL remains unchanged.",
            syntax = "rewrite [<matcher>] <to>",
            examples = """
                # Rewrite all requests to index.php
                rewrite * /index.php?{query}

                # Rewrite specific path
                rewrite /old-path /new-path
            """.trimIndent(),
        ),
        "root" to CaddyDirectiveDoc(
            name = "root",
            description = "Sets the root directory for file serving. All file operations are relative to this path.",
            syntax = "root [<matcher>] <path>",
            examples = """
                # Set root for all requests
                root * /var/www/html

                # Set root for specific paths
                root /static/* /var/www/static
            """.trimIndent(),
        ),
        "route" to CaddyDirectiveDoc(
            name = "route",
            description = "Groups directives into a route that is executed atomically. Useful for complex routing logic.",
            syntax = "route [<matcher>] {\n\t<directives...>\n}",
            examples = """
                route {
                    header X-Route "main"
                    respond "Hello from main route"
                }

                route /api/* {
                    header X-API "true"
                    reverse_proxy api-server:8000
                }
            """.trimIndent(),
        ),
        "tls" to CaddyDirectiveDoc(
            name = "tls",
            description = "Configures TLS for the site. Can specify certificates, key files, and other TLS options.",
            syntax = "tls [<email>|internal|<cert_file> <key_file>|off|<options...>] {\n\t<options...>\n}",
            examples = """
                # Automatic HTTPS with Let's Encrypt
                tls admin@example.com

                # Use custom certificates
                tls /path/to/cert.pem /path/to/key.pem

                # Disable TLS
                tls off

                # Advanced configuration
                tls {
                    protocols tls1.2 tls1.3
                    ciphers TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384 TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
                    client_auth {
                        mode require
                        trusted_ca_cert_file /path/to/ca.pem
                    }
                }
            """.trimIndent(),
        ),
        "try_files" to CaddyDirectiveDoc(
            name = "try_files",
            description = "Tries to serve files in the specified order. Useful for implementing fallbacks.",
            syntax = "try_files [<matcher>] <files...> [<status_code>]",
            examples = """
                # Try index.html, then fall back to index.php
                try_files {path} index.html index.php

                # Try requested path, then return 404
                try_files {path} 404
            """.trimIndent(),
        ),
        "uri" to CaddyDirectiveDoc(
            name = "uri",
            description = "Manipulates the request URI. Can strip prefixes, replace path components, and more.",
            syntax = "uri [<matcher>] <directive> [<options...>]",
            examples = """
                # Strip a prefix
                uri strip_prefix /api

                # Replace path
                uri replace /old-path /new-path
            """.trimIndent(),
        ),

        "basic_auth" to CaddyDirectiveDoc(
            name = "basic_auth",
            description = "Enforces HTTP Basic Authentication.",
            syntax = "basic_auth [<matcher>] [<hash_algorithm> [<realm>]] {\n\t<username> <hashed_password_base64>\n\t...\n}",
            examples = """
                basic_auth {
                    user JDJhJDEwJDFqZzYxTXE4MnRwRTJtMzVRTjhzT3VpYUxXWGN3cHZMUDdVeHpQL0xaYnIwZUxjZDhVUXVh
                }
            """.trimIndent(),
        ),
        "error" to CaddyDirectiveDoc(
            name = "error",
            description = "Trigger an error.",
            syntax = "error [<matcher>] <status_code> [<error_message>]",
            examples = """
                # Return a 404 error
                error 404

                # Return a custom error message
                error 500 "Internal Server Error: Database connection failed"
            """.trimIndent(),
        ),
        "file_server" to CaddyDirectiveDoc(
            name = "file_server",
            description = "Serve files from disk.",
            syntax = "file_server [<matcher>] [browse] [<root>] {\n\t<options...>\n}",
            examples = """
                # Simple file server
                file_server

                # With directory browsing enabled
                file_server browse

                # With custom options
                file_server {
                    root /var/www
                    hide .git
                    index index.html index.php
                }
            """.trimIndent(),
        ),
        "forward_auth" to CaddyDirectiveDoc(
            name = "forward_auth",
            description = "Delegate authentication to an external service.",
            syntax = "forward_auth [<matcher>] <uri> {\n\t<options...>\n}",
            examples = """
                forward_auth https://auth.example.com/verify {
                    uri /auth
                    copy_headers Authorization
                }
            """.trimIndent(),
        ),
        "fs" to CaddyDirectiveDoc(
            name = "fs",
            description = "Set the file system to use for file I/O.",
            syntax = "fs [<matcher>] <fs_module> [<args...>]",
            examples = """
                # Use a local file system
                fs local

                # Use a custom file system module
                fs custom_module arg1 arg2
            """.trimIndent(),
        ),
        "handle_errors" to CaddyDirectiveDoc(
            name = "handle_errors",
            description = "Defines routes for handling errors.",
            syntax = "handle_errors [<matcher>] {\n\t<directives...>\n}",
            examples = """
                handle_errors {
                    respond "An error occurred: {err.status_code} {err.message}" {err.status_code}
                }

                handle_errors {
                    @404 expression {err.status_code} == 404
                    rewrite @404 /custom-404.html
                    file_server
                }
            """.trimIndent(),
        ),
        "handle_path" to CaddyDirectiveDoc(
            name = "handle_path",
            description = "Like handle, but strips path prefix.",
            syntax = "handle_path [<matcher>] <path> {\n\t<directives...>\n}",
            examples = """
                handle_path /api {
                    reverse_proxy localhost:9000
                }

                # The above is equivalent to:
                handle /api/* {
                    uri strip_prefix /api
                    reverse_proxy localhost:9000
                }
            """.trimIndent(),
        ),
        "import" to CaddyDirectiveDoc(
            name = "import",
            description = "Include snippets or files.",
            syntax = "import <pattern>",
            examples = """
                # Import a specific file
                import config/common.conf

                # Import all files in a directory
                import config/*.conf
            """.trimIndent(),
        ),
        "intercept" to CaddyDirectiveDoc(
            name = "intercept",
            description = "Intercept responses written by other handlers.",
            syntax = "intercept [<matcher>] {\n\t<directives...>\n}",
            examples = """
                intercept {
                    @html header Content-Type text/html*
                    replace @html "Copyright 2020" "Copyright 2023"
                }
            """.trimIndent(),
        ),
        "invoke" to CaddyDirectiveDoc(
            name = "invoke",
            description = "Invoke a named route.",
            syntax = "invoke <route_name>",
            examples = """
                # Define a named route
                @logging {
                    path /api/*
                }

                # Invoke the named route
                invoke @logging
            """.trimIndent(),
        ),
        "log_append" to CaddyDirectiveDoc(
            name = "log_append",
            description = "Append a field to the access log.",
            syntax = "log_append [<matcher>] <field> <value>",
            examples = """
                log_append client_ip {remote_host}

                log_append response_time {duration}
            """.trimIndent(),
        ),
        "log_skip" to CaddyDirectiveDoc(
            name = "log_skip",
            description = "Skip access logging for matched requests.",
            syntax = "log_skip [<matcher>]",
            examples = """
                # Skip logging for health checks
                @health path /health
                log_skip @health

                # Skip logging for static assets
                @static path *.css *.js *.png *.jpg
                log_skip @static
            """.trimIndent(),
        ),
        "log_name" to CaddyDirectiveDoc(
            name = "log_name",
            description = "Override the logger name(s) to write to.",
            syntax = "log_name [<matcher>] <logger_name>",
            examples = """
                log_name api_logger

                @api path /api/*
                log_name @api api_requests
            """.trimIndent(),
        ),
        "map" to CaddyDirectiveDoc(
            name = "map",
            description = "Maps an input value to one or more outputs.",
            syntax = "map [<matcher>] <source> <destination> {\n\t<input> <output>\n\t...\n\t[default <output>]\n}",
            examples = """
                map {path} {lang} {
                    /en/* en
                    /fr/* fr
                    /de/* de
                    default en
                }

                map {remote_host} {rate_limit} {
                    192.168.1.* 100
                    10.0.0.* 200
                    default 50
                }
            """.trimIndent(),
        ),
        "method" to CaddyDirectiveDoc(
            name = "method",
            description = "Change the HTTP method internally.",
            syntax = "method [<matcher>] <methods...>",
            examples = """
                # Change all requests to GET
                method GET

                # Change POST requests to PUT
                @post method POST
                method @post PUT
            """.trimIndent(),
        ),
        "metrics" to CaddyDirectiveDoc(
            name = "metrics",
            description = "Configures the Prometheus metrics exposition endpoint.",
            syntax = "metrics [<matcher>] [<path>]",
            examples = """
                # Expose metrics at /metrics
                metrics

                # Expose metrics at a custom path
                metrics /prometheus
            """.trimIndent(),
        ),
        "push" to CaddyDirectiveDoc(
            name = "push",
            description = "Push content to the client using HTTP/2 server push.",
            syntax = "push [<matcher>] [<resources...>]",
            examples = """
                # Push CSS and JS files
                push /style.css /script.js

                # Push resources for HTML responses
                @html header Content-Type text/html*
                push @html /style.css /script.js /logo.png
            """.trimIndent(),
        ),
        "redir" to CaddyDirectiveDoc(
            name = "redir",
            description = "Issues an HTTP redirect to the client.",
            syntax = "redir [<matcher>] <to> [<code>]",
            examples = """
                # Permanent redirect (301)
                redir https://example.com 301

                # Temporary redirect (302, default)
                redir /new-path

                # Redirect with path preservation
                redir https://example.com{uri}
            """.trimIndent(),
        ),
        "request_body" to CaddyDirectiveDoc(
            name = "request_body",
            description = "Manipulates request body.",
            syntax = "request_body [<matcher>] {\n\t<options...>\n}",
            examples = """
                request_body {
                    max_size 10MB
                }
            """.trimIndent(),
        ),
        "request_header" to CaddyDirectiveDoc(
            name = "request_header",
            description = "Manipulates request headers.",
            syntax = "request_header [<matcher>] [[+|-]<field> [<value>...]]... {\n\t[+|-]<field> [<value>...]\n\t...\n}",
            examples = """
                # Add a header
                request_header +X-Real-IP {remote_host}

                # Remove a header
                request_header -Referer

                # Set multiple headers
                request_header {
                    +X-Forwarded-Proto https
                    +X-Forwarded-Host {host}
                    -User-Agent
                }
            """.trimIndent(),
        ),
        "respond" to CaddyDirectiveDoc(
            name = "respond",
            description = "Writes a hard-coded response to the client.",
            syntax = "respond [<matcher>] [<status>] [<body>] {\n\t<options...>\n}",
            examples = """
                # Simple response
                respond "Hello, world!"

                # Custom status code
                respond 201 "Resource created"

                # With headers
                respond 200 "OK" {
                    header Content-Type application/json
                    header X-Custom-Header "Custom Value"
                }
            """.trimIndent(),
        ),
        "templates" to CaddyDirectiveDoc(
            name = "templates",
            description = "Execute templates on the response.",
            syntax = "templates [<matcher>] {\n\t<options...>\n}",
            examples = """
                templates {
                    mime text/html text/plain
                }

                templates {
                    include *.html *.txt
                    delimiters "{{" "}}"
                }
            """.trimIndent(),
        ),
        "tracing" to CaddyDirectiveDoc(
            name = "tracing",
            description = "Integration with OpenTelemetry tracing.",
            syntax = "tracing [<matcher>] {\n\t<options...>\n}",
            examples = """
                tracing {
                    span root_span
                    tags {
                        service caddy
                        environment production
                    }
                }
            """.trimIndent(),
        ),
        "vars" to CaddyDirectiveDoc(
            name = "vars",
            description = "Set arbitrary variables.",
            syntax = "vars [<matcher>] [<name> <value>]... {\n\t<name> <value>\n\t...\n}",
            examples = """
                # Set a single variable
                vars version 1.0

                # Set multiple variables
                vars {
                    environment production
                    region us-west
                    debug false
                }
            """
        )
    )

    fun getDocumentation(directiveName: String): CaddyDirectiveDoc? {
        return directives[directiveName]
    }
}