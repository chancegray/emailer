package edu.usf.cims.emailer

import groovy.sql.Sql


def getVIPGroups(Sql sql) {
	def groups = [:]
	sql.eachRow('select gid, label from vip_group') {
		groups.put(it.gid, it.label)
	}
	groups
}

def getExpiredVIPs(Sql sql) {
	def vips = sql.rows("""select
	vg.gid as gid,
	v.fname as fname,
	v.lname as lname,
	vgm.created_dt as created_dt,
	vgm.created_vipid as created_vipid,
	vgm.expiration_dt as expiration_dt
	from vip_group_member vgm 
	LEFT JOIN vip_group vg ON vgm.gid=vg.gid 
	LEFT JOIN vip v on v.vipid=vgm.vipid
	where 
	expiration_dt between '2013-01-03 00:00:00' and '2013-01-20 00:00:00' 
	and vgm.function='M'
	""")
}


