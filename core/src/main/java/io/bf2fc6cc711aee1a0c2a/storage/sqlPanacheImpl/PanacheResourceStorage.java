package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl;

import io.bf2fc6cc711aee1a0c2a.logging.Logged;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryDeploymentNotFoundException;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryNotFoundException;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.StorageConflictException;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 * @author Fabian Martinez
 */
@ApplicationScoped
@Transactional
@Logged
public class PanacheResourceStorage implements ResourceStorage {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    PanacheRegistryRepository registryRepository;

    @Inject
    PanacheRegistryDeploymentRepository deploymentRepository;

    @Override
    public boolean createOrUpdateRegistry(Registry registry) throws StorageConflictException {
        requireNonNull(registry);
        Optional<Registry> existing = empty();
        if (registry.getId() != null) {
            existing = registryRepository.findByIdOptional(registry.getId());
        }
        try {
            registryRepository.persistAndFlush(registry);
        } catch (PersistenceException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw StorageConflictException.create("Registry");
            }
        }
        return existing.isEmpty();
    }

    @Override
    public Optional<Registry> getRegistryById(Long id) {
        requireNonNull(id);
        return ofNullable(registryRepository.findById(id));
    }

    @Override
    public List<Registry> getAllRegistries() {
        return registryRepository.listAll();
    }

    @Override
    public void deleteRegistry(Long id) throws RegistryNotFoundException {
        Registry registry = getRegistryById(id)
                .orElseThrow(() -> RegistryNotFoundException.create(id));
        registryRepository.delete(registry);
    }

    //*** RegistryDeployment

    @Override
    public boolean createOrUpdateRegistryDeployment(RegistryDeployment deployment) throws StorageConflictException {
        requireNonNull(deployment); // TODO Is this necessary if using @Valid?
        Optional<RegistryDeployment> existing = empty();
        if (deployment.getId() != null) {
            existing = deploymentRepository.findByIdOptional(deployment.getId());
        }
        try {
            deploymentRepository.persistAndFlush(deployment);
        } catch (PersistenceException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw StorageConflictException.create("Registry Deployment");
            }
        }
        return existing.isEmpty();
    }

    @Override
    public List<RegistryDeployment> getAllRegistryDeployments() {
        return deploymentRepository.listAll();
    }

    @Override
    public Optional<RegistryDeployment> getRegistryDeploymentById(Long id) {
        requireNonNull(id);
        return ofNullable(deploymentRepository.findById(id));
    }

    @Override
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException {
        RegistryDeployment rd = getRegistryDeploymentById(id)
                .orElseThrow(() -> RegistryDeploymentNotFoundException.create(id));
        deploymentRepository.delete(rd);
    }
}
