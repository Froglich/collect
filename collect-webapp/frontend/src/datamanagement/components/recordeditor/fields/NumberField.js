import { FormControl, InputLabel, Select } from '@material-ui/core'
import React from 'react'
import { Input } from 'reactstrap'

import AbstractField from './AbstractField'
import FieldLoadingSpinner from './FieldLoadingSpinner'
import FieldValidationTooltip from './FieldValidationTooltip'

export default class NumberField extends AbstractField {
  constructor(props) {
    super(props)

    this.fieldId = `number-field-${new Date().getTime()}`

    this.onTextValueChange = this.onTextValueChange.bind(this)
    this.onUnitChange = this.onUnitChange.bind(this)
  }

  extractValueFromProps() {
    const { fieldDef } = this.props
    const attr = this.getAttribute()

    if (!attr) {
      return null
    }

    const attrDef = fieldDef.attributeDefinition
    const precisions = attrDef.precisions

    const unitId = attr.fields[1].value
    let selectedUnitId
    if (unitId) {
      selectedUnitId = unitId
    } else if (precisions.length) {
      const defaultPrecision = precisions.find((precision) => precision.defaultPrecision) || precisions[0]
      selectedUnitId = defaultPrecision.unitId
    }

    return { value: attr.fields[0].value, unit: selectedUnitId }
  }

  onTextValueChange(event) {
    const { value } = this.state
    const valueUpdated = { ...value, value: Number(event.target.value) }
    this.onAttributeUpdate({ value: valueUpdated })
  }

  onUnitChange(event) {
    const { value } = this.state
    const valueUpdated = { ...value, unit: Number(event.target.value) }
    this.onAttributeUpdate({ value: valueUpdated })
  }

  render() {
    const { fieldDef } = this.props
    const { dirty, value: valueState, errors, warnings } = this.state
    const { value, unit: unitId } = valueState || {}
    const text = value || ''
    const attrDef = fieldDef.attributeDefinition
    const precisions = attrDef.precisions
    const hasPrecisions = precisions.length > 0

    return (
      <div>
        <div style={hasPrecisions ? { display: 'grid', gridTemplateColumns: '1fr 150px' } : null}>
          <Input
            id={this.fieldId}
            type="number"
            invalid={Boolean(errors || warnings)}
            className={warnings ? 'warning' : ''}
            value={text}
            onChange={this.onTextValueChange}
          />
          <FieldValidationTooltip target={this.fieldId} errors={errors} warnings={warnings} />

          {hasPrecisions && (
            <FormControl>
              <InputLabel>Unit</InputLabel>
              <Select variant="outlined" native value={unitId} onChange={this.onUnitChange} label="Unit">
                {precisions.map((precision) => {
                  const unit = attrDef.survey.units.find((unit) => unit.id === precision.unitId)
                  return (
                    <option key={unit.id} value={unit.id}>
                      {unit.label}
                    </option>
                  )
                })}
              </Select>
            </FormControl>
          )}
        </div>
        {dirty && <FieldLoadingSpinner />}
      </div>
    )
  }
}
