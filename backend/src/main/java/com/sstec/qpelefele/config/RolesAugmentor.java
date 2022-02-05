package com.sstec.qpelefele.config;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.function.Supplier;
@ApplicationScoped
public class RolesAugmentor implements SecurityIdentityAugmentor {

    @ConfigProperty(name = "aws.iac.cognitoAdminGroupName")
    String cognitoAdminGroupName;

    @ConfigProperty(name = "aws.iac.cognitoRegularUserGroupName")
    String cognitoRegularUserGroupName;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        return Uni.createFrom().item(build(identity));
    }

    private Supplier<SecurityIdentity> build(SecurityIdentity identity) {
        if(identity.isAnonymous()) {
            return () -> identity;
        } else {
            JWTCallerPrincipal principal = (JWTCallerPrincipal) identity.getPrincipal();
            String cognitoGroup = principal.getClaim("cognito:groups").toString();
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);

            if (cognitoGroup.contains(this.cognitoRegularUserGroupName)) {
                builder.addRole(OIDCRoleType.OIDCRole.REGULAR_USER);
            } else if (cognitoGroup.contains(this.cognitoAdminGroupName)) {
                builder.addRole(OIDCRoleType.OIDCRole.ADMIN);
            }

            return builder::build;
        }
    }
}
