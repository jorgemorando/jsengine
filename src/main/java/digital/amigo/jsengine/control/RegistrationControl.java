package digital.amigo.jsengine.control;

import digital.amigo.jsengine.exception.RuleEngineException;

import java.util.List;

/**
 * Interfaz de control de registro de entidades en el motor de reglas JavaScript. <br>
 * Establece las capacidades de registro de reglas y escenarios en el motor.
 *
 * @author jorge.morando
 * @see RulesControl
 */
public interface RegistrationControl<T> {

    /**
     * Registra la entidad en el administrador del motor. Si la entidad ya se encuentra registrada, registra una nueva versión de la misma. <br>
     * Ambas versiones estarán disponibles para su utilización
     *
     * @param entity the entity to register in the engine
     */
    void register(T entity);

    /**
     * Registra una nueva versión de la entidad en el administrador del motor. Si la versión que se está registrando ya existe, lanza una excepción RuleEngineException
     *
     * @param entity the entity to register in the engine
     * @throws RuleEngineException when entity is null or the version of entity is already registered
     */
    void register(T entity, int version);

    /**
     * Registra las entidades en el administrador del motor. Se invoca al método {@link #register} por cada una.
     *
     * @param entities a list of the entities to register in the engine
     */
    void register(List<T> entities);

    /**
     * Verifica que la entidad se encuentre registrada en el administrador del motor
     *
     * @param entityName of the entity to register in the engine
     * @return boolean true if it's registered
     */
    boolean isRegistered(String entityName);

}
