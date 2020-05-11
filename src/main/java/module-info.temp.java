/*
module ir.ceno.main {

    exports ir.ceno; // Export ir.ceno to module spring.boot.devtools
    opens ir.ceno; // Open ir.ceno to module spring.core
    opens ir.ceno.config;

    requires java.desktop;
    requires java.logging;
    requires java.management;
    requires java.sql;
    requires java.persistence;
    requires java.annotation;
    requires java.validation;
    requires spring.core;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.context;
    requires spring.context.support;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.web;
    requires spring.webmvc;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.tx;
    requires tomcat.embed.core;
    requires org.hibernate.orm.core;
    requires org.hibernate.search.orm;
    requires org.hibernate.search.engine;
    requires slf4j.api;
    requires org.aspectj.weaver;
    requires com.github.benmanes.caffeine;
    requires com.rometools.rome;
    requires persian.date.time;

    // NOTE: Add to build file (needed by lombok when making the app modular):
    // implementation 'org.mapstruct:mapstruct-processor:1.3.1.Final'
    requires lombok;

    // NOTE: Also update hibernate-search transitive dependency like this:
    // compile('org.hibernate:hibernate-search-orm:5.11.5.Final') {
    //   // To resolve the problem with split packages (packages with same name in two modules) in
    //   // lucene.core and lucene.misc modules when making the application modular
    //    exclude group: 'org.apache.lucene', module: 'lucene-misc'
    // }
    requires lucene.core;
}
*/
