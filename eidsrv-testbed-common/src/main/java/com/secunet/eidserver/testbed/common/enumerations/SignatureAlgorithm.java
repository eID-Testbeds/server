package com.secunet.eidserver.testbed.common.enumerations;

public enum SignatureAlgorithm {
	ECDSA_SHA_1("SHA1WITHCVC-ECDSA"),
	ECDSA_SHA_224("SHA224WITHCVC-ECDSA"),
	ECDSA_SHA_256("SHA256WITHCVC-ECDSA");
	
	private final String algorithmName;

	private SignatureAlgorithm(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * Returns the name of the signature algorithm
	 * 
	 * @return
	 */
	public String getAlgorithmName() {
		return algorithmName;
	}
	
	/**
	 * Returns the enum value that represents the signature algorithm with the
	 * given name
	 * 
	 * @param url
	 */
	public static SignatureAlgorithm getFromEnumName(String name) {
		for (SignatureAlgorithm x : SignatureAlgorithm.values()) {
			if (name != null && name.equals(x.toString())) {
				return x;
			}
		}
		return null;
	}

	/**
	 * Returns the enum value that represents the signature algorithm with the
	 * given algorithm name
	 * 
	 * @param url
	 */
	public static SignatureAlgorithm getFromAlgorithmName(String name) {
		for (SignatureAlgorithm x : SignatureAlgorithm.values()) {
			if (name != null && name.equals(x.getAlgorithmName())) {
				return x;
			}
		}
		return null;
	}

}
