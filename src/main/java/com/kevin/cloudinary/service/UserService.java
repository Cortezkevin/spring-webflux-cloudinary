package com.kevin.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kevin.cloudinary.documents.User;
import com.kevin.cloudinary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final Cloudinary cloudinary;

    public Flux<User> getAll(){
        return repository.findAll();
    }

    public Mono<User> getById(String id){
        return repository.findById(id);
    }

    public Mono<User> create(MultiValueMap<String, Part> multiValueMap){
        try {
            return getFormData( multiValueMap ).flatMap( data -> {

                String username = (String) data.get("username");
                String email = (String) data.get("email");
                File photo = (File) data.get("photo");

                return uploadPhoto( photo, username ).flatMap( photoUrl -> repository.save(User.builder().username(username).email(email).photoUrl(photoUrl).build()));
            });
        }catch ( Exception e){
            return Mono.error( new Exception("Ocurrio un error" + e.getMessage()));
        }
    }

    public Mono<User> update(User user, String id){
        return repository.findById( id ).hasElement()
                .flatMap( exists -> exists ? repository.save(new User(id, user.getUsername(), user.getEmail(), user.getPhotoUrl())) : Mono.error(new Exception("Usuario no existe")));
    }

    public Mono<Map<String, Object>> delete(String id){
        return repository.findById( id ).hasElement()
                .flatMap( exists -> {
                    if( exists ){
                        Map<String, Object> res = new HashMap<>();
                        res.put("success","Usuario eliminado");
                        return repository.deleteById(id).then( Mono.just(res) );
                    }
                    return Mono.error(new Exception("Usuario no existe"));
                });
    }

    private Mono<String> uploadPhoto( File uploadFile, String uploadFileName ){
        try {
            Map params = ObjectUtils.asMap(
                    "upload_preset", "java_api", //UPLOAD_PRESET -> NOMBRE DE LA CARPETA DE CLOUDINARY DONDE SE GUARDARA LA IMAGEN
                    "public_id", uploadFileName //PUBLIC_ID -> NOMBRE CON EL QUE SE GUARDARA LA IMAGEN
            );

            Map res = cloudinary.uploader().upload( uploadFile, params); //RESPUESTA DE CLOUDINARY
            return Mono.just(res.get("secure_url").toString());
        } catch (IOException e) {
            return Mono.error(new Exception("Ocurrio un error al subir la foto"));
        }
    }

    private Mono<Map<String, Object>> getFormData( MultiValueMap<String, Part> multiValueMap ){
        try {
            Map<String, Part> mapPart = multiValueMap.toSingleValueMap();
            Map<String, Object> formData = new HashMap<>();
            mapPart.forEach(( key, value ) -> {
                if( value.toString().contains("DefaultFormFieldPart")){
                    String data = ((FormFieldPart) value).value();
                    formData.put(key, data);
                }else{
                    FilePart filePart = (FilePart) value;
                    File file = new File(System.getProperty("java.io.tmpdir")+"/"+filePart.filename());
                    filePart.transferTo( file ).then(Mono.just(true)).flatMap( unused -> Mono.just( file )).subscribe( res -> formData.put(key, res) );
                }
            });
            return Mono.just(formData);
        }catch (Exception e ){
            return Mono.error(new Exception("Ocurrio un error al procesar la informacion"));
        }
    }
}
