/*
 * Copyright (c) 2010-2013 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.repo.sql.data.common.id;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class RAExtBooleanId implements Serializable {

    private String ownerOid;
    private Integer ownerId;
    private Boolean value;
    private String name;

    public String getOwnerOid() {
        return ownerOid;
    }

    public void setOwnerOid(String ownerOid) {
        this.ownerOid = ownerOid;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RAExtBooleanId raLongId = (RAExtBooleanId) o;

        if (name != null ? !name.equals(raLongId.name) : raLongId.name != null) return false;
        if (ownerId != null ? !ownerId.equals(raLongId.ownerId) : raLongId.ownerId != null) return false;
        if (ownerOid != null ? !ownerOid.equals(raLongId.ownerOid) : raLongId.ownerOid != null) return false;
        if (value != null ? !value.equals(raLongId.value) : raLongId.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ownerOid != null ? ownerOid.hashCode() : 0;
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RABooleanId{" +
                "ownerOid='" + ownerOid + '\'' +
                ", ownerId=" + ownerId +
                ", value=" + value +
                '}';
    }
}
