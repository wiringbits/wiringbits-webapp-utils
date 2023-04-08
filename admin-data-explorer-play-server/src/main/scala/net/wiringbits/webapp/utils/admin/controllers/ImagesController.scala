package net.wiringbits.webapp.utils.admin.controllers

import net.wiringbits.webapp.utils.admin.services.AdminService
import org.slf4j.LoggerFactory
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ImagesController @Inject() (
    adminService: AdminService
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def find(tableName: String, columnName: String, imageId: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get image for $tableName, id = $imageId")
      image <- adminService.findImage(tableName, columnName, imageId)
    } yield Ok.sendFile(image).withHeaders(("Content-Type", "image/png"))
  }
}
