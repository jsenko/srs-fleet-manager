package io.bf2fc6cc711aee1a0c2a.storage;

import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
// TODO: Unify Exception vs. Optional?
public interface ResourceStorage {

    //*** Registry

    boolean createOrUpdateRegistry(@Valid Registry registry) throws StorageConflictException;

    Optional<Registry> getRegistryById(@NotNull Long id);

    List<Registry> getAllRegistries();

    void deleteRegistry(@NotNull Long id) throws RegistryNotFoundException;

    //*** RegistryDeployment

    boolean createOrUpdateRegistryDeployment(@Valid RegistryDeployment rd) throws StorageConflictException;

    List<RegistryDeployment> getAllRegistryDeployments();

    Optional<RegistryDeployment> getRegistryDeploymentById(@NotNull Long id);

    void deleteRegistryDeployment(@NotNull Long id) throws RegistryDeploymentNotFoundException;
}
