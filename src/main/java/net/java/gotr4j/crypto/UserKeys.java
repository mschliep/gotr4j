package net.java.gotr4j.crypto;

import net.java.gotr4j.GotrUser;

import java.security.PublicKey;
import java.util.*;

public class UserKeys {

    private Map<GotrUser, UserSecret> secrets = new HashMap<GotrUser, UserSecret>();

    public void put(GotrUser user, byte[] secret, PublicKey signingPublicKey){
        secrets.put(user, new UserSecret(user, secret, signingPublicKey));
    }

    public void put(UserSecret secret){
        secrets.put(secret.getUser(), secret);
    }

    public UserSecret get(GotrUser user){
        return secrets.get(user);
    }

    public byte[] getSecret(GotrUser user){
        UserSecret secret = secrets.get(user);
        if(secret!= null){
            return secret.getSecret();
        }
        else{
            return null;
        }
    }


    public List<UserSecret> getOrderedSecrets(){
        List<UserSecret> result = new ArrayList<UserSecret>(secrets.values());

        Collections.sort(result, new Comparator<UserSecret>() {
            @Override
            public int compare(UserSecret a, UserSecret b) {
                return a.getUser().compareTo(b.getUser());
            }
        });

        return result;
    }

    public int size(){
        return secrets.size();
    }

}
