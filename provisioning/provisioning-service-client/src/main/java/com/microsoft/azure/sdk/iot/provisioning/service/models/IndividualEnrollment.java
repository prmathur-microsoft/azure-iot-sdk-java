/**
 * Code generated by Microsoft (R) AutoRest Code Generator.
 * Changes may cause incorrect behavior and will be lost if the code is
 * regenerated.
 */

package com.microsoft.azure.sdk.iot.provisioning.service.models;

import org.joda.time.DateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The device enrollment record.
 */
public class IndividualEnrollment {
    /**
     * Capabilities of the device.
     */
    @JsonProperty(value = "capabilities")
    private DeviceCapabilities capabilities;

    /**
     * The registration ID is alphanumeric, lowercase, and may contain hyphens.
     */
    @JsonProperty(value = "registrationId", required = true)
    private String registrationId;

    /**
     * Desired IoT Hub device ID (optional).
     */
    @JsonProperty(value = "deviceId")
    private String deviceId;

    /**
     * Current registration status.
     */
    @JsonProperty(value = "registrationState", access = JsonProperty.Access.WRITE_ONLY)
    private IndividualEnrollmentRegistrationState registrationState;

    /**
     * Attestation method used by the device.
     */
    @JsonProperty(value = "attestation", required = true)
    private AttestationMechanism attestation;

    /**
     * The Iot Hub host name.
     */
    @JsonProperty(value = "iotHubHostName")
    private String iotHubHostName;

    /**
     * Initial device twin.
     */
    @JsonProperty(value = "initialTwin")
    private InitialTwin initialTwin;

    /**
     * The entity tag associated with the resource.
     */
    @JsonProperty(value = "etag")
    private String etag;

    /**
     * The provisioning status. Possible values include: 'enabled', 'disabled'.
     */
    @JsonProperty(value = "provisioningStatus")
    private String provisioningStatus;

    /**
     * The behavior when a device is re-provisioned to an IoT hub.
     */
    @JsonProperty(value = "reprovisionPolicy")
    private ReprovisionPolicy reprovisionPolicy;

    /**
     * The DateTime this resource was created.
     */
    @JsonProperty(value = "createdDateTimeUtc", access = JsonProperty.Access.WRITE_ONLY)
    private DateTime createdDateTimeUtc;

    /**
     * The DateTime this resource was last updated.
     */
    @JsonProperty(value = "lastUpdatedDateTimeUtc", access = JsonProperty.Access.WRITE_ONLY)
    private DateTime lastUpdatedDateTimeUtc;

    /**
     * The allocation policy of this resource. This policy overrides the tenant
     * level allocation policy for this individual enrollment or enrollment
     * group. Possible values include 'hashed': Linked IoT hubs are equally
     * likely to have devices provisioned to them, 'geoLatency':  Devices are
     * provisioned to an IoT hub with the lowest latency to the device.If
     * multiple linked IoT hubs would provide the same lowest latency, the
     * provisioning service hashes devices across those hubs, 'static' :
     * Specification of the desired IoT hub in the enrollment list takes
     * priority over the service-level allocation policy, 'custom': Devices are
     * provisioned to an IoT hub based on your own custom logic. The
     * provisioning service passes information about the device to the logic,
     * and the logic returns the desired IoT hub as well as the desired initial
     * configuration. We recommend using Azure Functions to host your logic.
     * Possible values include: 'hashed', 'geoLatency', 'static', 'custom'.
     */
    @JsonProperty(value = "allocationPolicy")
    private String allocationPolicy;

    /**
     * The list of names of IoT hubs the device(s) in this resource can be
     * allocated to. Must be a subset of tenant level list of IoT hubs.
     */
    @JsonProperty(value = "iotHubs")
    private List<String> iotHubs;

    /**
     * Custom allocation definition.
     */
    @JsonProperty(value = "customAllocationDefinition")
    private CustomAllocationDefinition customAllocationDefinition;

    /**
     * Get capabilities of the device.
     *
     * @return the capabilities value
     */
    public DeviceCapabilities capabilities() {
        return this.capabilities;
    }

    /**
     * Set capabilities of the device.
     *
     * @param capabilities the capabilities value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withCapabilities(DeviceCapabilities capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    /**
     * Get the registration ID is alphanumeric, lowercase, and may contain hyphens.
     *
     * @return the registrationId value
     */
    public String registrationId() {
        return this.registrationId;
    }

    /**
     * Set the registration ID is alphanumeric, lowercase, and may contain hyphens.
     *
     * @param registrationId the registrationId value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withRegistrationId(String registrationId) {
        this.registrationId = registrationId;
        return this;
    }

    /**
     * Get desired IoT Hub device ID (optional).
     *
     * @return the deviceId value
     */
    public String deviceId() {
        return this.deviceId;
    }

    /**
     * Set desired IoT Hub device ID (optional).
     *
     * @param deviceId the deviceId value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    /**
     * Get current registration status.
     *
     * @return the registrationState value
     */
    public IndividualEnrollmentRegistrationState registrationState() {
        return this.registrationState;
    }

    /**
     * Get attestation method used by the device.
     *
     * @return the attestation value
     */
    public AttestationMechanism attestation() {
        return this.attestation;
    }

    /**
     * Set attestation method used by the device.
     *
     * @param attestation the attestation value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withAttestation(AttestationMechanism attestation) {
        this.attestation = attestation;
        return this;
    }

    /**
     * Get the Iot Hub host name.
     *
     * @return the iotHubHostName value
     */
    public String iotHubHostName() {
        return this.iotHubHostName;
    }

    /**
     * Set the Iot Hub host name.
     *
     * @param iotHubHostName the iotHubHostName value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withIotHubHostName(String iotHubHostName) {
        this.iotHubHostName = iotHubHostName;
        return this;
    }

    /**
     * Get initial device twin.
     *
     * @return the initialTwin value
     */
    public InitialTwin initialTwin() {
        return this.initialTwin;
    }

    /**
     * Set initial device twin.
     *
     * @param initialTwin the initialTwin value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withInitialTwin(InitialTwin initialTwin) {
        this.initialTwin = initialTwin;
        return this;
    }

    /**
     * Get the entity tag associated with the resource.
     *
     * @return the etag value
     */
    public String etag() {
        return this.etag;
    }

    /**
     * Set the entity tag associated with the resource.
     *
     * @param etag the etag value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withEtag(String etag) {
        this.etag = etag;
        return this;
    }

    /**
     * Get the provisioning status. Possible values include: 'enabled', 'disabled'.
     *
     * @return the provisioningStatus value
     */
    public String provisioningStatus() {
        return this.provisioningStatus;
    }

    /**
     * Set the provisioning status. Possible values include: 'enabled', 'disabled'.
     *
     * @param provisioningStatus the provisioningStatus value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withProvisioningStatus(String provisioningStatus) {
        this.provisioningStatus = provisioningStatus;
        return this;
    }

    /**
     * Get the behavior when a device is re-provisioned to an IoT hub.
     *
     * @return the reprovisionPolicy value
     */
    public ReprovisionPolicy reprovisionPolicy() {
        return this.reprovisionPolicy;
    }

    /**
     * Set the behavior when a device is re-provisioned to an IoT hub.
     *
     * @param reprovisionPolicy the reprovisionPolicy value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withReprovisionPolicy(ReprovisionPolicy reprovisionPolicy) {
        this.reprovisionPolicy = reprovisionPolicy;
        return this;
    }

    /**
     * Get the DateTime this resource was created.
     *
     * @return the createdDateTimeUtc value
     */
    public DateTime createdDateTimeUtc() {
        return this.createdDateTimeUtc;
    }

    /**
     * Get the DateTime this resource was last updated.
     *
     * @return the lastUpdatedDateTimeUtc value
     */
    public DateTime lastUpdatedDateTimeUtc() {
        return this.lastUpdatedDateTimeUtc;
    }

    /**
     * Get the allocation policy of this resource. This policy overrides the tenant level allocation policy for this individual enrollment or enrollment group. Possible values include 'hashed': Linked IoT hubs are equally likely to have devices provisioned to them, 'geoLatency':  Devices are provisioned to an IoT hub with the lowest latency to the device.If multiple linked IoT hubs would provide the same lowest latency, the provisioning service hashes devices across those hubs, 'static' : Specification of the desired IoT hub in the enrollment list takes priority over the service-level allocation policy, 'custom': Devices are provisioned to an IoT hub based on your own custom logic. The provisioning service passes information about the device to the logic, and the logic returns the desired IoT hub as well as the desired initial configuration. We recommend using Azure Functions to host your logic. Possible values include: 'hashed', 'geoLatency', 'static', 'custom'.
     *
     * @return the allocationPolicy value
     */
    public String allocationPolicy() {
        return this.allocationPolicy;
    }

    /**
     * Set the allocation policy of this resource. This policy overrides the tenant level allocation policy for this individual enrollment or enrollment group. Possible values include 'hashed': Linked IoT hubs are equally likely to have devices provisioned to them, 'geoLatency':  Devices are provisioned to an IoT hub with the lowest latency to the device.If multiple linked IoT hubs would provide the same lowest latency, the provisioning service hashes devices across those hubs, 'static' : Specification of the desired IoT hub in the enrollment list takes priority over the service-level allocation policy, 'custom': Devices are provisioned to an IoT hub based on your own custom logic. The provisioning service passes information about the device to the logic, and the logic returns the desired IoT hub as well as the desired initial configuration. We recommend using Azure Functions to host your logic. Possible values include: 'hashed', 'geoLatency', 'static', 'custom'.
     *
     * @param allocationPolicy the allocationPolicy value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withAllocationPolicy(String allocationPolicy) {
        this.allocationPolicy = allocationPolicy;
        return this;
    }

    /**
     * Get the list of names of IoT hubs the device(s) in this resource can be allocated to. Must be a subset of tenant level list of IoT hubs.
     *
     * @return the iotHubs value
     */
    public List<String> iotHubs() {
        return this.iotHubs;
    }

    /**
     * Set the list of names of IoT hubs the device(s) in this resource can be allocated to. Must be a subset of tenant level list of IoT hubs.
     *
     * @param iotHubs the iotHubs value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withIotHubs(List<String> iotHubs) {
        this.iotHubs = iotHubs;
        return this;
    }

    /**
     * Get custom allocation definition.
     *
     * @return the customAllocationDefinition value
     */
    public CustomAllocationDefinition customAllocationDefinition() {
        return this.customAllocationDefinition;
    }

    /**
     * Set custom allocation definition.
     *
     * @param customAllocationDefinition the customAllocationDefinition value to set
     * @return the IndividualEnrollment object itself.
     */
    public IndividualEnrollment withCustomAllocationDefinition(CustomAllocationDefinition customAllocationDefinition) {
        this.customAllocationDefinition = customAllocationDefinition;
        return this;
    }

}
