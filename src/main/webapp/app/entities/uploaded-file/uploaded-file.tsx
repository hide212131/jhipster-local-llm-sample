import React, { useState, useEffect, useCallback } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { openFile, byteSize, Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { IUploadedFile } from 'app/shared/model/uploaded-file.model';
import { useDropzone } from 'react-dropzone';

import { createEntity, getEntities } from './uploaded-file.reducer';

export const UploadedFile = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const uploadedFileList = useAppSelector(state => state.uploadedFile.entities);
  const loading = useAppSelector(state => state.uploadedFile.loading);
  const updateing = useAppSelector(state => state.uploadedFile.updating);
  const updateSuccess = useAppSelector(state => state.uploadedFile.updateSuccess);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  const onDrop = useCallback(
    (acceptedFiles: File[]) => {
      (async () => {
        if (acceptedFiles.length === 0) {
          return;
        }

        const file = acceptedFiles[0];
        const formData: IUploadedFile = {};

        const buffer = await file.arrayBuffer();
        formData.data = arrayBufferToBase64(buffer);
        formData.filename = file.name;
        formData.dataContentType = file.type;

        dispatch(createEntity(formData));
      })();
    },
    [dispatch],
  );

  function arrayBufferToBase64(buffer: ArrayBuffer): string {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary);
  }

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    noClick: true,
    noKeyboard: true,
  });

  useEffect(() => {
    if (updateing === false && updateSuccess) {
      navigate('/uploaded-file');
    }
  }, [updateSuccess, updateing, navigate]);

  return (
    <div {...getRootProps()}>
      <input {...getInputProps()} />
      <h2 id="uploaded-file-heading" data-cy="UploadedFileHeading">
        Uploaded Files
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={updateing}>
            <FontAwesomeIcon icon="sync" spin={updateing} /> Refresh
          </Button>
        </div>
      </h2>
      Drug and Drop File in this area
      <div className="table-responsive">
        {uploadedFileList && uploadedFileList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('filename')}>
                  Filename <FontAwesomeIcon icon={getSortIconByFieldName('filename')} />
                </th>
                <th className="hand" onClick={sort('data')}>
                  Data <FontAwesomeIcon icon={getSortIconByFieldName('data')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {uploadedFileList.map((uploadedFile, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{uploadedFile.filename}</td>
                  <td>
                    {uploadedFile.data ? (
                      <div>
                        <span>
                          {uploadedFile.dataContentType}, {byteSize(uploadedFile.data)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        onClick={() => (window.location.href = `/uploaded-file/${uploadedFile.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Uploaded Files found</div>
        )}
      </div>
    </div>
  );
};

export default UploadedFile;
