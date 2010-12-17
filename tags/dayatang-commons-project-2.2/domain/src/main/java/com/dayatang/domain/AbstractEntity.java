/**
 * 
 */
package com.dayatang.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * 抽象实体类，可作为所有领域实体的基类，提供ID和版本属性。
 * 
 * @author yang
 * 
 */
@MappedSuperclass
public abstract class AbstractEntity implements Entity {

	private static final long serialVersionUID = 2364892694478974374L;

	private static EntityRepository repository;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@Transient
	private Set<String> errorCodes = new HashSet<String>();

	/**
	 * 获得实体的标识
	 * 
	 * @return 实体的标识
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置实体的标识
	 * 
	 * @param id
	 *            要设置的实体标识
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获得实体的版本号。持久化框架以此实现乐观锁。
	 * 
	 * @return 实体的版本号
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 设置实体的版本号。持久化框架以此实现乐观锁。
	 * 
	 * @param version
	 *            要设置的版本号
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isNew() {
		return getId() == null;
	}

	/**
	 * 校验实体。如果子类决定覆盖该方法，在方法体的最后应调用super.validate()。
	 */
	protected void validate() {
		if (hasValidateError()) {
			throw new ValidationException(errorCodes);
		}
	}

	/**
	 * 添加一条验证错误消息
	 * 
	 * @param message
	 */
	protected void addValidateError(String message) {
		errorCodes.add(message);
	}

	/**
	 * 判断是否存在验证错误。
	 * 
	 * @return
	 */
	protected boolean hasValidateError() {
		return !errorCodes.isEmpty();
	}

	public static EntityRepository getRepository() {
		if (repository == null) {
			repository = InstanceFactory.getInstance(EntityRepository.class);
		}
		return repository;
	}

	public static void setRepository(EntityRepository repository) {
		AbstractEntity.repository = repository;
	}

	public void save() throws ValidationException {
		validate();
		getRepository().save(this);
	}

	public void remove() {
		getRepository().remove(this);
	}

	public static <T extends Entity> boolean exists(Class<T> clazz, Serializable id) {
		return getRepository().exists(clazz, id);
	}

	public static <T extends Entity> T get(Class<T> clazz, Serializable id) {
		return getRepository().get(clazz, id);
	}

	public static <T extends Entity> T getUnmodified(Class<T> clazz, T entity) {
		return getRepository().getUnmodified(clazz, entity);
	}

	public static <T extends Entity> T load(Class<T> clazz, Serializable id) {
		return getRepository().load(clazz, id);
	}

	public static <T extends Entity> List<T> findAll(Class<T> clazz) {
		return getRepository().find(QuerySettings.create(clazz));
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object other);

	@Override
	public abstract String toString();
}
