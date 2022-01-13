package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.react.mod.ChangeEvent
import net.wiringbits.facades.reactRouterDom.{mod => reactRouterDom}
import net.wiringbits.webapp.utils.api.models.AdminGetTableMetadata
import org.scalajs.dom.{HTMLInputElement, HTMLTextAreaElement}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.|

@react object Pagination {
  case class Props(response: AdminGetTableMetadata.Response)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val history = reactRouterDom.useHistory()
    val numberOfRecords = props.response.count
    val pageSize = props.response.limit
    val currentPage = props.response.offSet / pageSize

    var url = props.response.name

    def handlePageChange(pageNumber: Double): Unit = {
      val offSet = pageNumber.toInt * pageSize
      url += s"?limit=$pageSize&offset=$offSet"
      history.push(url)
    }

    def handleRowChange(event: ChangeEvent[HTMLTextAreaElement | HTMLInputElement]): Unit = {
      val rowsPerPage = event.target_ChangeEvent.asInstanceOf[js.Dynamic].value

      url += s"?limit=$rowsPerPage"
      history.push(url)
    }

    PaginationWidget(
      numberOfRecords,
      currentPage,
      (_, pageNumber) => handlePageChange(pageNumber),
      event => handleRowChange(event),
      pageSize
    )

  }
}
