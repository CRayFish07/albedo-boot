package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.domain.base.IdEntity;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.annotation.DictType;
import com.albedo.java.util.annotation.SearchField;
import com.albedo.java.util.base.Collections3;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mybatis.annotations.*;

import java.util.List;
import java.util.Set;

import static org.apache.ibatis.ognl.OgnlRuntime.NotFound;

/**
 * Copyright 2013 albedo All right reserved Author lijie Created on 2013-10-23 下午4:32:52
 */
@Entity(table = "SYS_ROLE_T")
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Role extends IdEntity {

	private static final long serialVersionUID = 1L;
	// 数据范围（1：所有数据；2：所在机构及以下数据；3：所在机构数据；4：仅本人数据；5：按明细设置）
	/*** 数据范围(所有数据) */
	public static final Integer DATA_SCOPE_ALL = 1;
	/*** 数据范围(所在机构及以下数据) */
	public static final Integer DATA_SCOPE_ORG_AND_CHILD = 2;
	/*** 数据范围(所在机构数据) */
	public static final Integer DATA_SCOPE_ORG = 3;
	/*** 数据范围(仅本人数据) */
	public static final Integer DATA_SCOPE_SELF = 4;
	/*** 数据范围(按明细设置) */
	public static final Integer DATA_SCOPE_CUSTOM = 5;
	
	public static final String F_SORT = "sort";
	public static final String F_NAME = "name";
	public static final String F_SYSDATA = "sysData";

	/*** 角色名称 */
	@Column(name = "name_")
	@SearchField
	private String name;
	/*** 名称全拼 */
	@Column(name = "en_")
	@SearchField
	private String en;
	/*** 工作流组用户组类型（security-role：管理员、assignment：可进行任务分配、user：普通用户） */
	@Column(name = "type_")
	private String type;
	/*** 组织ID */
	@Column(name = "org_id")
	private String orgId;
	
	@ManyToOne
    @JoinColumn(name = "org_id", updatable = false,insertable = false)
    @ApiModelProperty(hidden=true)
    private Org org;
	
	/*** 是否系统数据  0 是 1否*/
	@Column(name = "sys_data") @DictType(name="sys_yes_no")
	private Integer sysData;
	/*** 可查看的数据范围 */
	@Column(name = "data_scope")
	private Integer dataScope;
	@Column(name = "sort_")
	private Integer sort;
	/*** 组织机构 */
	@ManyToMany
	@JoinTable(name = "SYS_ROLE_ORG_T",
			joinColumns = { @JoinColumn(name = "role_id") },
			inverseJoinColumns = { @JoinColumn(name = "org_id") })
	@JSONField(serialize=false)
	@ApiModelProperty(hidden=true)
	private Set<Org> orgs = Sets.newHashSet();
	/*** 操作权限 */
	@ManyToMany
	@JoinTable(name = "SYS_ROLE_MODULE_T",
			joinColumns = { @JoinColumn(name = "role_id") },
			inverseJoinColumns = { @JoinColumn(name = "module_id") })
	@ApiModelProperty(hidden=true)
	private Set<Module> modules = Sets.newHashSet();

	/*** 拥有用户列表 */
	@ManyToMany
	@JSONField(serialize=false)
	@ApiModelProperty(hidden=true)
	@JoinTable(name = "sys_user_role_t",
			joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id_")},
			inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id_")})
	private Set<User> users = Sets.newHashSet();

	@Transient
	@JSONField(serialize=false)
	private List<String> moduleIdList;
	@Transient @JSONField(serialize=false)
	private List<String> orgIdList;
	

	public Role(String id) {
		this.setId(id);
	}

	public Role(String id, Set<Module> modules) {
		this.setId(id);
		this.modules = modules;
	}

	public Role(String id, String name) {
		this.setId(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Integer getSysData() {
		return sysData;
	}

	public void setSysData(Integer sysData) {
		this.sysData = sysData;
	}

	public Integer getDataScope() {
		return dataScope;
	}

	public void setDataScope(Integer dataScope) {
		this.dataScope = dataScope;
	}


	public Set<Module> getModules() {
		return modules;
	}

	public void setModules(Set<Module> modules) {
		this.modules = modules;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEn() {
		return en;
	}

	public void setEn(String en) {
		this.en = en;
	}


	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Org> getOrgs() {
		return orgs;
	}

	public void setOrgs(Set<Org> orgs) {
		this.orgs = orgs;
	}


	public List<String> getOrgIdList() {
		if (PublicUtil.isEmpty(orgIdList) && PublicUtil.isNotEmpty(orgs)) {
			orgIdList = Lists.newArrayList();
			orgs.forEach(o -> {if(PublicUtil.isNotEmpty(o))orgIdList.add(o.getId());});
		}
		return orgIdList;
	}

	public void setOrgIdList(List<String> orgIdList) {
		this.orgIdList = orgIdList;
		if (PublicUtil.isNotEmpty(orgIdList)) {
			orgs = Sets.newHashSet();
			orgIdList.forEach(o -> {if(PublicUtil.isNotEmpty(o))orgs.add(new Org(o));});
		}
	}
	public String getOrgIds() {
		return Collections3.convertToString(getOrgIdList(), ",");
	}
	
	public List<String> getModuleIdList() {
		if (PublicUtil.isEmpty(moduleIdList) && PublicUtil.isNotEmpty(modules)) {
			moduleIdList = Lists.newArrayList();
			modules.forEach(m -> {if(PublicUtil.isNotEmpty(m))moduleIdList.add(m.getId());});
		}
		return moduleIdList;
	}
	public String getModuleIds() {
		return Collections3.convertToString(getModuleIdList(), ",");
	}
	public void setModuleIdList(List<String> moduleIdList) {
		this.moduleIdList = moduleIdList;
		if (PublicUtil.isNotEmpty(moduleIdList)) {
			modules = Sets.newHashSet();
			moduleIdList.forEach(m -> {if(PublicUtil.isNotEmpty(m))modules.add(new Module(m));});
		}
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}


}
