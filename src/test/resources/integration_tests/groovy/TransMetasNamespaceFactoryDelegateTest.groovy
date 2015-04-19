/*
 * Example Groovy integration test that deploys the module that this project builds.
 *
 * Quite often in integration tests you want to deploy the same module for all tests and you don't want tests
 * to start before the module has been deployed.
 *
 * This test demonstrates how to do that.
 */


import com.github.brosander.kettle.vertx.KettleVerticle
import com.github.brosander.kettle.vertx.namespace.action.ActionHandler
import com.github.brosander.kettle.vertx.namespace.generic.ActionMapNamespace
import com.github.brosander.kettle.vertx.namespace.kettle.TransMetasActionMapNamespace
import org.vertx.groovy.testtools.VertxTests

import static org.vertx.testtools.VertxAssert.*

// And import static the VertxTests script
// The test methods must being with "test"

def testCreateTransMeta() {
    vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['name': 'newTrans', 'action': 'create', 'namespace': ['transMetas']], { reply ->
        try {
            assertTrue("Got " + reply.body().toString(), reply.body().toString().startsWith(TransMetasActionMapNamespace.SUCCESSFULLY_CREATED_TRANS_META))
            vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['name': 'newTrans', 'action': 'delete', 'namespace': ['transMetas']], { reply2 ->
                try {
                    assertTrue("Got " + reply2.body().toString(), reply2.body().toString().startsWith(TransMetasActionMapNamespace.SUCCESSFULLY_REMOVED_TRANS_META))
                } finally {
                    testComplete();
                }
            })
        } catch (Exception e) {
            testComplete()
        }
    })
}

def testCreateTransMetaFilenameNoFilename() {
    vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['name': 'newTrans', 'action': 'create', 'source': 'filename', 'namespace': ['transMetas']], { reply ->
        try {
            assertTrue("Got " + reply.body().toString(), reply.body().getMessage().startsWith(TransMetasActionMapNamespace.MUST_SPECIFY_FILENAME_TO_LOAD_FOR_FILENAME_TRANS_CREATE))
        } finally {
            testComplete()
        }
    })
}

def testCreateTransMetaFilenameNoFile() {
    vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['name': 'newTrans', 'action': 'create', 'source': 'filename', 'filename': 'fake_file.ktr', 'namespace': ['transMetas']], { reply ->
        try {
            assertTrue("Got " + reply.body().toString(), reply.body().getMessage().startsWith(TransMetasActionMapNamespace.ERROR_LOADING_TRANSFORMATION))
        } finally {
            testComplete()
        }
    })
}

def testDeleteTransMetaNotExisting() {
    vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['name': 'nonExistentTrans', 'action': 'delete', 'namespace': ['transMetas']], { reply ->
        try {
            assertTrue("Got " + reply.body().toString(), reply.body().getMessage().startsWith(TransMetasActionMapNamespace.TRANS_META_NOT_FOUND))
        } finally {
            testComplete()
        }
    })
}

def testNoName() {
    vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['namespace': ['transMetas']], { reply ->
        try {
            assertTrue("Got " + reply.body().toString(), reply.body().getMessage().startsWith(ActionHandler.MUST_SPECIFY_NAME_TO_OPERATE_ON))
        } finally {
            testComplete()
        }
    })
}

// Make sure you initialize
VertxTests.initialize(this)

// The script is execute for each test, so this will deploy the module for each one
// Deploy the module - the System property `vertx.modulename` will contain the name of the module so you
// don't have to hardecode it in your tests
container.deployModule(System.getProperty("vertx.modulename"), { asyncResult ->
    // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
    assertTrue(asyncResult.succeeded)
    assertNotNull("deploymentID should not be null", asyncResult.result())
    // If deployed correctly then start the tests!
    VertxTests.startTests(this)
})