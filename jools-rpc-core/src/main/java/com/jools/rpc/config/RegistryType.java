package com.jools.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Jools He
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum RegistryType {

    /***
     * ETCD
     */
    ETCD("etcd");

    private String type;
}
