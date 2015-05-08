package integration_tests.groovy

import com.github.brosander.kettle.vertx.KettleVerticle
import com.github.brosander.kettle.vertx.namespace.kettle.RootNamespace
import com.github.brosander.kettle.vertx.namespace.kettle.TransMetaNamespace
import com.github.brosander.kettle.vertx.namespace.kettle.TransMetasNamespace
import org.vertx.groovy.testtools.VertxTests

import static org.vertx.testtools.VertxAssert.*

// And import static the VertxTests script
// The test methods must being with "test"

def testCreateTransMetaFilename() {
    vertx.eventBus.registerHandler(TransMetaNamespace.KETTLE_VERTICAL_TRANS_STATUS, { message ->
        if (message.body().type == TransMetaNamespace.FINISHED) {
            testComplete()
        } else if (message.body().type == TransMetaNamespace.STARTED) {

        } else {
            handleThrowable(new Exception("We should only get started and finished notifications"))
        }
    })
    vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['name': 'newTrans', 'action': 'loadFile', 'filename': 'src/test/resources/transformations/test.ktr', 'namespace': [RootNamespace.TRANS_METAS]], { reply ->
        try {
            assertTrue("Got " + reply.body().toString(), reply.body().toString().startsWith(TransMetasNamespace.SUCCESSFULLY_CREATED_TRANS_META))
            vertx.eventBus.send(KettleVerticle.KETTLE_VERTICLE, ['action': 'start', 'namespace': [RootNamespace.TRANS_METAS, 'newTrans']], { reply2 ->
                assertTrue("Got " + reply2.body().toString(), reply2.body().toString().startsWith(TransMetaNamespace.SUCCESSFULLY_STARTED_TRANSFORMATION))
            })

        } catch (Exception e) {
            handleThrowable(e)
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