package com.kevin.cloudinary.routes;

import com.kevin.cloudinary.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    private static final String PATH = "user";

    @Bean
    RouterFunction<ServerResponse> userRoute(UserHandler userHandler){
        return RouterFunctions
                .route( RequestPredicates.GET(PATH), userHandler::getAll  )
                .andRoute( RequestPredicates.GET(PATH + "/{id}"), userHandler::getById)
                                                                            //ESPECIFICAR EN LA RUTA EL TIPO DE CONTENIDO
                .andRoute( RequestPredicates.POST(PATH).and(RequestPredicates.contentType(MediaType.MULTIPART_FORM_DATA)), userHandler::create )
                .andRoute( RequestPredicates.PUT(PATH + "/{id}"), userHandler::update)
                .andRoute( RequestPredicates.DELETE(PATH + "/{id}"), userHandler::delete);
    }

}
