package io.ebean;

import io.ebean.bean.EntityBean;

/**
 * Provides finder functionality for use with "Dependency Injection style" use of Ebean.
 * <p>
 * <pre>{@code
 *
 * @Repository
 * public class CustomerRepository extends BeanRepository<Long,Customer> {
 *
 *   @Inject
 *   public CustomerRepository(EbeanServer server) {
 *     super(Customer.class, server);
 *   }
 *
 *   // ... add customer specific finders and persist logic
 *
 *   public List<Customer> findByName(String nameStart) {
 *     return query().where()
 *             .istartsWith("name", nameStart)
 *             .findList();
 *   }
 *
 * }
 * }</pre>
 *
 * @param <I> The ID type
 * @param <T> The Bean type
 */
public abstract class BeanRepository<I, T> extends BeanFinder<I, T> {

  /**
   * Create with the given bean type and EbeanServer instance.
   * <p>
   * Typically users would extend BeanRepository rather than BeanFinder.
   * </p>
   * <pre>{@code
   *
   *   @Inject
   *   public CustomerRepository(EbeanServer server) {
   *     super(Customer.class, server);
   *   }
   *
   * }</pre>
   *
   * @param type   The bean type
   * @param server The EbeanServer instance typically created via Spring factory or equivalent
   */
  protected BeanRepository(Class<T> type, EbeanServer server) {
    super(type, server);
  }

  /**
   * Marks the entity bean as dirty.
   * <p>
   * This is used so that when a bean that is otherwise unmodified is updated the version
   * property is updated.
   * <p>
   * An unmodified bean that is saved or updated is normally skipped and this marks the bean as
   * dirty so that it is not skipped.
   * <p>
   * <pre>{@code
   *
   * Customer customer = customerRepository.byId(id);
   *
   * // mark the bean as dirty so that a save() or update() will
   * // increment the version property
   *
   * customerRepository.markAsDirty(customer);
   * customerRepository.save(customer);
   *
   * }</pre>
   *
   * @see EbeanServer#markAsDirty(Object)
   */
  public void markAsDirty(T bean) {
    db().markAsDirty(bean);
  }

  /**
   * Mark the property as unset or 'not loaded'.
   * <p>
   * This would be used to specify a property that we did not wish to include in a stateless update.
   * </p>
   * <pre>{@code
   *
   *   // populate an entity bean from JSON or whatever
   *   Customer customer = ...;
   *
   *   // mark the email property as 'unset' so that it is not
   *   // included in a 'stateless update'
   *   customerRepository.markPropertyUnset(customer, "email");
   *
   *   customerRepository.update(customer);
   *
   * }</pre>
   *
   * @param propertyName the name of the property on the bean to be marked as 'unset'
   */
  public void markPropertyUnset(T bean, String propertyName) {
    ((EntityBean) bean)._ebean_getIntercept().setPropertyLoaded(propertyName, false);
  }

  /**
   * Insert or update this entity depending on its state.
   * <p>
   * Ebean will detect if this is a new bean or a previously fetched bean and perform either an
   * insert or an update based on that.
   *
   * @see EbeanServer#save(Object)
   */
  public void save(T bean) {
    db().save(bean);
  }

  /**
   * Update this entity.
   *
   * @see EbeanServer#update(Object)
   */
  public void update(T bean) {
    db().update(bean);
  }

  /**
   * Insert this entity.
   *
   * @see EbeanServer#insert(Object)
   */
  public void insert(T bean) {
    db().insert(bean);
  }

  /**
   * Delete this bean.
   * <p>
   * This will return true if the bean was deleted successfully or JDBC batch is being used.
   * </p>
   * <p>
   * If there is no current transaction one will be created and committed for
   * you automatically.
   * </p>
   * <p>
   * If the Bean does not have a version property (or loaded version property) and
   * the bean does not exist then this returns false indicating that nothing was
   * deleted. Note that, if JDBC batch mode is used then this always returns true.
   * </p>
   *
   * @see EbeanServer#delete(Object)
   */
  public boolean delete(T bean) {
    return db().delete(bean);
  }

  /**
   * Delete a bean permanently without soft delete.
   * <p>
   * This is used when the bean contains a <code>@SoftDelete</code> property and we
   * want to perform a hard/permanent delete.
   * </p>
   *
   * @see EbeanServer#deletePermanent(Object)
   */
  public boolean deletePermanent(T bean) {
    return db().deletePermanent(bean);
  }

  /**
   * Merge this entity using the default merge options.
   * <p>
   * Ebean will detect if this is a new bean or a previously fetched bean and perform either an
   * insert or an update based on that.
   *
   * @see EbeanServer#merge(Object)
   */
  public void merge(T bean) {
    db().merge(bean);
  }

  /**
   * Merge this entity using the specified merge options.
   * <p>
   * Ebean will detect if this is a new bean or a previously fetched bean and perform either an
   * insert or an update based on that.
   *
   * @see EbeanServer#merge(Object, MergeOptions)
   */
  public void merge(T bean, MergeOptions options) {
    db().merge(bean, options);
  }

  /**
   * Refreshes this entity from the database.
   *
   * @see EbeanServer#refresh(Object)
   */
  public void refresh(T bean) {
    db().refresh(bean);
  }
}
