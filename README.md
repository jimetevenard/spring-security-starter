Spring Boot - projet demo Notes

# Création et sécurisation d'un Service REST avec Spring Boot

Nous allons créer une application minimaliste avec Spring Boot.

Cette application exposera deux points d'API en REST :

* Resource `/hello` : retourne une chaine de caractère de bienvenue.  
  Cette resource doit être accessible à tous, sans identification
* Resource `/liste` : Liste fictive de données (ici, un simple *array* de String)
  Cette resource ne doit être accessible qu'aux utilisateurs identifiés.

Nous utilisons le Starter Spring Boot <https://start.spring.io/> pour initialiser notre projet.

## Dependencies

Dépendences à selection dans le [Starter Spring Boot](https://start.spring.io/)

* **Spring Web** : Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.
* **Spring Security** : Highly customizable authentication and access-control framework for Spring applications.
* **MySQL Driver** (Utile plus tard) : MySQL JDBC and R2DBC driver.

## Lets go !

Une classe avec méthode main a été automatiquement générée :

````
package com.jimetevenard.jimnotesback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JimNotesBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(JimNotesBackApplication.class, args);
	}

}
````

C'est le principe de Spring Boot : le serveur TomCat est déjà embarqué en dépendance, nous n'avons rien d'autre à faire qu'à lancer la méthode `main()` !

## Rest Controller

Création de deux REST Controller.

Nous allons créer deux point d'API, `/hello` qui doit être accessible à tous, et `/liste` pour les utilisateurs authentifiés.

doc : <https://spring.io/guides/gs/rest-service/>

### Controleur *Hello*

````
package com.jimetevenard.jimnotesback.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/hello")
	public String hello() {
		return "Hello, Jim !";
	}

}
````

### Contrôleur *Liste*

````
package com.jimetevenard.jimnotesback.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListeController {
	
	
	@GetMapping("/liste")
	public String[] list(){
		return new String[]{"foo","bar","baz"};
	}

}
````

**Note** : Spring se charge à notre place de la conversion de l'objet retourné vers le format *JSON*.

## Gestion de la sécurité / authentification

Spring Security est en dépendance (Maven) de notre application.

Accédons à notre application depuis un navigateur : `http://locahost:8080/`  
(Le port par défaut est 8080, vérifier en console java si ce n'est pas le cas)

Quelque soit la requête, je suis redirigé vers un formulaire de login (généré par Spring) !

Il faut configurer *Spring Security* pour obtenir le comportement désiré.

### Configuration de *Spring Security*

Docs : 

* Starter Guide : <https://spring.io/guides/gs/securing-web/>
* Référence complète : <https://docs.spring.io/spring-security/site/docs/5.4.6/reference/html5/>

Nous allons créer une classe de configuration (cf. exemple dans le [guide](https://spring.io/guides/gs/securing-web/)

#### Configuration minimaliste

##### Ouvrir toutes les requêtes à tout le monde

Classe de configuration minimaliste pour autoriser toutes les requêtes à tout le monde

````
package com.jimetevenard.jimnotesback;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Nous autorisons  l'accès libre à toutes les requêtes
		http.authorizeRequests().anyRequest().permitAll();

	}
}
````

##### Utilisation d'un utilisateur fictif

Première étape, être capable de s'authentifier avec un login/mot de passe.

Nous allons, pour le moment, utiliser un utilisateur fictif, codé en dur.

Nous implémentons dans la classe de configuratiopn une méthode - annotée [@bean](https://docs.spring.io/spring-javaconfig/docs/1.0.0.M4/reference/html/ch02s02.html) - pour retourner un [UserDetailsService](https://www.codeflow.site/fr/article/spring-security-authentication-with-a-database).



````
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username("jim")
				.password("jim")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(user);

````

##### Filtrage des requêtes

Nous allons maintenant filtrer les requêtes de sorte que :

- Les requêtes vers `hello` soient libres d'accès
- Les requêtes vers toutes les autres URLs soient authentifiées.
- Nous utilisons la méthode [formLogin()](https://docs.spring.io/spring-security/site/docs/5.4.6/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html#formLogin--) pour utiliser le mécanisme d'authentication par formulaire de Spring.

Améliorons notre méthode `configure()` : 

````
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		
		http
			.authorizeRequests()
				.antMatchers("/", "/hello").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.and()
			.logout()
				.permitAll();
				
	}

````

