<?php
/**
 * SlimRouter
 *
 * PHP version 5
 *
 * @category Class
 * @package  OpenAPIServer
 * @author   OpenAPI Generator team
 * @link     https://github.com/openapitools/openapi-generator
 */

/** 
 * OpenAPI Petstore
 *
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\
 * OpenAPI spec version: 1.0.0
 * Generated by: https://github.com/openapitools/openapi-generator.git
 */

/**
 * NOTE: This class is auto generated by the openapi generator program.
 * https://github.com/openapitools/openapi-generator
 * Do not edit the class manually.
 */
namespace OpenAPIServer;

use OpenAPIServer\Api\AnotherFakeApi;
use OpenAPIServer\Api\FakeApi;
use OpenAPIServer\Api\FakeClassnameTags123Api;
use OpenAPIServer\Api\PetApi;
use OpenAPIServer\Api\StoreApi;
use OpenAPIServer\Api\UserApi;
use Slim\App;
use Psr\Container\ContainerInterface;
use InvalidArgumentException;

/**
 * SlimRouter Class Doc Comment
 *
 * PHP version 5
 *
 * @category Class
 * @package  OpenAPIServer\Api
 * @author   OpenAPI Generator team
 * @link     https://github.com/openapitools/openapi-generator
 */
class SlimRouter {

    /**
     * @var $slimApp Slim\App instance
     */
    private $slimApp;

    /**
     * Class constructor
     *
     * @param ContainerInterface|array $container Either a ContainerInterface or an associative array of app settings
     * @throws InvalidArgumentException when no container is provided that implements ContainerInterface
     */
    public function __construct($container = []) {
        $app = new App($container);

        $app->PATCH('/v2/another-fake/dummy', AnotherFakeApi::class . ':123testSpecialTags');
        $app->POST('/v2/fake/outer/boolean', FakeApi::class . ':fakeOuterBooleanSerialize');
        $app->POST('/v2/fake/outer/composite', FakeApi::class . ':fakeOuterCompositeSerialize');
        $app->POST('/v2/fake/outer/number', FakeApi::class . ':fakeOuterNumberSerialize');
        $app->POST('/v2/fake/outer/string', FakeApi::class . ':fakeOuterStringSerialize');
        $app->PUT('/v2/fake/body-with-file-schema', FakeApi::class . ':testBodyWithFileSchema');
        $app->PUT('/v2/fake/body-with-query-params', FakeApi::class . ':testBodyWithQueryParams');
        $app->PATCH('/v2/fake', FakeApi::class . ':testClientModel');
        $app->POST('/v2/fake', FakeApi::class . ':testEndpointParameters');
        $app->GET('/v2/fake', FakeApi::class . ':testEnumParameters');
        $app->POST('/v2/fake/inline-additionalProperties', FakeApi::class . ':testInlineAdditionalProperties');
        $app->GET('/v2/fake/jsonFormData', FakeApi::class . ':testJsonFormData');
        $app->PATCH('/v2/fake_classname_test', FakeClassnameTags123Api::class . ':testClassname');
        $app->POST('/v2/pet', PetApi::class . ':addPet');
        $app->GET('/v2/pet/findByStatus', PetApi::class . ':findPetsByStatus');
        $app->GET('/v2/pet/findByTags', PetApi::class . ':findPetsByTags');
        $app->PUT('/v2/pet', PetApi::class . ':updatePet');
        $app->DELETE('/v2/pet/{petId}', PetApi::class . ':deletePet');
        $app->GET('/v2/pet/{petId}', PetApi::class . ':getPetById');
        $app->POST('/v2/pet/{petId}', PetApi::class . ':updatePetWithForm');
        $app->POST('/v2/pet/{petId}/uploadImage', PetApi::class . ':uploadFile');
        $app->POST('/v2/fake/{petId}/uploadImageWithRequiredFile', PetApi::class . ':uploadFileWithRequiredFile');
        $app->GET('/v2/store/inventory', StoreApi::class . ':getInventory');
        $app->POST('/v2/store/order', StoreApi::class . ':placeOrder');
        $app->DELETE('/v2/store/order/{order_id}', StoreApi::class . ':deleteOrder');
        $app->GET('/v2/store/order/{order_id}', StoreApi::class . ':getOrderById');
        $app->POST('/v2/user', UserApi::class . ':createUser');
        $app->POST('/v2/user/createWithArray', UserApi::class . ':createUsersWithArrayInput');
        $app->POST('/v2/user/createWithList', UserApi::class . ':createUsersWithListInput');
        $app->GET('/v2/user/login', UserApi::class . ':loginUser');
        $app->GET('/v2/user/logout', UserApi::class . ':logoutUser');
        $app->DELETE('/v2/user/{username}', UserApi::class . ':deleteUser');
        $app->GET('/v2/user/{username}', UserApi::class . ':getUserByName');
        $app->PUT('/v2/user/{username}', UserApi::class . ':updateUser');

        $this->slimApp = $app;
    }

    /**
     * Returns Slim Framework instance
     * @return App
     */
    public function getSlimApp() {
        return $this->slimApp;
    }
}
